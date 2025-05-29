package com.airwallex.android.view

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.airwallex.android.R
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.Airwallex.PaymentResultListener
import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexRecurringSession
import com.airwallex.android.core.AirwallexRecurringWithIntentSession
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.extension.putIfNotNull
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.Bank
import com.airwallex.android.core.model.CardScheme
import com.airwallex.android.core.model.DisablePaymentConsentParams
import com.airwallex.android.core.model.DynamicSchemaField
import com.airwallex.android.core.model.DynamicSchemaFieldType
import com.airwallex.android.core.model.Page
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.core.model.PaymentMethodTypeInfo
import com.airwallex.android.core.model.RetrieveAvailablePaymentConsentsParams
import com.airwallex.android.core.model.RetrieveAvailablePaymentMethodParams
import com.airwallex.android.ui.checkout.AirwallexCheckoutViewModel
import com.airwallex.android.view.util.filterRequiredFields
import com.airwallex.android.view.util.findWithType
import com.airwallex.android.view.util.getSinglePaymentMethodOrNull
import com.airwallex.android.view.util.needCountryCode
import com.airwallex.android.view.util.toPaymentFlow
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

internal class PaymentMethodsViewModel(
    application: Application,
    private val airwallex: Airwallex,
    internal val session: AirwallexSession
) : AirwallexCheckoutViewModel(application, airwallex, session) {

    val pageName: String = "payment_method_list"

    private val _paymentFlowStatus = MutableLiveData<PaymentFlowStatus>()
    val paymentFlowStatus: LiveData<PaymentFlowStatus> = _paymentFlowStatus

    private val _paymentMethodResult = MutableLiveData<PaymentMethodResult>()
    val paymentMethodResult: LiveData<PaymentMethodResult> = _paymentMethodResult
    
    // Cache for schema data by payment method type
    private val schemaDataCache = mutableMapOf<AvailablePaymentMethodType, SchemaData>()

    // Map for additional params. Currently only used for country code in Enum type fields.
    private val additionalParams = mutableMapOf<String, String>()

    private val paymentIntent: PaymentIntent? by lazy {
        when (session) {
            is AirwallexPaymentSession -> {
                session.paymentIntent
            }

            is AirwallexRecurringWithIntentSession -> {
                session.paymentIntent
            }

            is AirwallexRecurringSession -> {
                null
            }

            else -> {
                throw Exception("Not supported session $session")
            }
        }
    }

    private val customerId: String? by lazy {
        when (session) {
            is AirwallexPaymentSession -> {
                session.paymentIntent.customerId
            }

            is AirwallexRecurringWithIntentSession -> {
                session.paymentIntent.customerId
            }

            is AirwallexRecurringSession -> {
                session.customerId
            }

            else -> {
                throw Exception("Not supported session $session")
            }
        }
    }

    private val clientSecret: String? by lazy {
        when (session) {
            is AirwallexPaymentSession, is AirwallexRecurringWithIntentSession -> {
                paymentIntent?.clientSecret
            }

            is AirwallexRecurringSession -> {
                session.clientSecret
            }

            else -> {
                null
            }
        }
    }

    private val hidePaymentConsents: Boolean by lazy {
        when (session) {
            is AirwallexPaymentSession -> {
                session.hidePaymentConsents
            }
            else -> {
                false
            }
        }
    }

    val schemaButtonTitle = if (session is AirwallexRecurringSession) {
        application.getString(R.string.airwallex_confirm)
    } else {
        application.getString(R.string.airwallex_pay_now)
    }

    fun confirmPaymentIntent(paymentConsent: PaymentConsent) {
        if (session is AirwallexPaymentSession) {
            airwallex.confirmPaymentIntent(
                session, paymentConsent,
                object : PaymentResultListener {
                    override fun onCompleted(status: AirwallexPaymentStatus) {
                        _paymentFlowStatus.value = PaymentFlowStatus.PaymentStatus(status)
                        trackPaymentSuccess(status, paymentConsent.paymentMethod?.type)
                    }
                }
            )
        } else {
            _paymentFlowStatus.value = PaymentFlowStatus.PaymentStatus(
                AirwallexPaymentStatus.Failure(AirwallexCheckoutException(message = "confirm with paymentConsent only support AirwallexPaymentSession"))
            )
        }
    }

    fun checkoutWithCvc(paymentConsent: PaymentConsent, cvc: String) = viewModelScope.launch {
        val paymentMethod = paymentConsent.paymentMethod
        if (paymentMethod == null) {
            _paymentFlowStatus.value = PaymentFlowStatus.PaymentStatus(
                AirwallexPaymentStatus.Failure(AirwallexCheckoutException(message = "checkout with paymentConsent without paymentMethod"))
            )
            return@launch
        }
        if (session is AirwallexPaymentSession) {
            checkout(
                paymentMethod = paymentMethod,
                paymentConsentId = paymentConsent.id,
                cvc = cvc,
            ).also {
                trackPaymentSuccess(it, paymentMethod.type)
                _paymentFlowStatus.value = PaymentFlowStatus.PaymentStatus(it)
            }
        } else {
            _paymentFlowStatus.value = PaymentFlowStatus.PaymentStatus(
                AirwallexPaymentStatus.Failure(AirwallexCheckoutException(message = "checkout with paymentConsent only support AirwallexPaymentSession"))
            )
        }
    }

    fun checkoutWithSchema(
        paymentMethod: PaymentMethod,
        additionalInfo: Map<String, String>,
        typeInfo: PaymentMethodTypeInfo
    ) = viewModelScope.launch {
        AirwallexLogger.info("PaymentMethodsViewModel checkoutWithSchema, type = ${paymentMethod.type}")
        checkout(paymentMethod, additionalInfo, typeInfo.toPaymentFlow(transactionMode))
            .also {
                trackPaymentSuccess(it, paymentMethod.type)
                _paymentFlowStatus.value = PaymentFlowStatus.PaymentStatus(it)
            }
    }

    fun checkoutWithSchema(paymentMethodType: AvailablePaymentMethodType) = viewModelScope.launch {
        AirwallexLogger.info("PaymentMethodsViewModel checkoutWithSchema, type = ${paymentMethodType.name}")
        val paymentMethod = PaymentMethod.Builder()
            .setType(paymentMethodType.name)
            .build()
        paymentMethod.type?.let { type ->
            AirwallexLogger.info("PaymentMethodsViewModel get more payment Info fields on one-off flow.")
            checkout(paymentMethod).also {
                trackPaymentSuccess(it, paymentMethod.type)
                _paymentFlowStatus.value = PaymentFlowStatus.PaymentStatus(it)
            }
        }
    }

    fun checkoutWithGooglePay() {
        viewModelScope.launch {
            AirwallexLogger.info("PaymentMethodsViewModel checkoutWithGooglePay")
            checkoutGooglePay().also {
                trackPaymentSuccess(it, PaymentMethodType.GOOGLEPAY.value)
                _paymentFlowStatus.value = PaymentFlowStatus.PaymentStatus(it)
            }
        }
    }

    fun fetchPaymentMethodsAndConsents() = viewModelScope.launch {
        val result = fetchAvailablePaymentMethodsAndConsents()
        result.fold(
            onSuccess = { methodsAndConsents ->
                val availableMethodTypes = methodsAndConsents.first
                val availablePaymentConsents = methodsAndConsents.second
                AirwallexLogger.info("PaymentMethodsViewModel fetchPaymentMethodsAndConsents availableMethodTypes = $availableMethodTypes, availablePaymentConsents = $availablePaymentConsents")

                // skip straight to the individual card screen?
                val singleCardPaymentMethod =
                    availableMethodTypes.getSinglePaymentMethodOrNull(availablePaymentConsents)
                // only one payment method and it's Card.
                if (singleCardPaymentMethod != null) {
                    _paymentMethodResult.value =
                        PaymentMethodResult.Skip(singleCardPaymentMethod.cardSchemes ?: emptyList())
                } else {
                    _paymentMethodResult.value =
                        PaymentMethodResult.Show(Pair(availableMethodTypes, availablePaymentConsents))
                }
            },
            onFailure = {
                _paymentFlowStatus.value = PaymentFlowStatus.ErrorAlert(it.message ?: it.toString())
            }
        )
    }

    fun trackPaymentSuccess(status: AirwallexPaymentStatus, paymentType: String?) {
        if (status is AirwallexPaymentStatus.Success) {
            trackPaymentSuccess(paymentType)
        }
    }

    fun trackCardPaymentSuccess() {
        AnalyticsLogger.logAction(
            PAYMENT_SUCCESS,
            mapOf(PAYMENT_METHOD to PaymentMethodType.CARD.value)
        )
    }

    fun trackCardPaymentSelection() {
        AnalyticsLogger.logAction(
            PAYMENT_SELECT,
            mapOf(PAYMENT_METHOD to PaymentMethodType.CARD.value)
        )
    }

    fun trackPaymentSuccess(paymentType: String?) {
        AnalyticsLogger.logAction(
            PAYMENT_SUCCESS,
            mutableMapOf<String, String>().apply {
                putIfNotNull(PAYMENT_METHOD, paymentType)
            }
        )
    }

    fun trackPaymentSelection(paymentMethodType: String?) {
        paymentMethodType?.takeIf { it.isNotEmpty() }?.let { type ->
            AnalyticsLogger.logAction(PAYMENT_SELECT, mapOf(PAYMENT_METHOD to type))
        }
    }

    suspend fun fetchAvailablePaymentMethodsAndConsents():
            Result<Pair<List<AvailablePaymentMethodType>, List<PaymentConsent>>> {
        val secret = clientSecret.takeIf { !it.isNullOrBlank() }
            ?: return Result.failure(AirwallexCheckoutException(message = "Client secret is empty or blank"))
        return supervisorScope {
            val intentId = (session as? AirwallexPaymentSession)?.paymentIntent?.id
            AirwallexLogger.info("PaymentMethodsViewModel fetchAvailablePaymentMethodsAndConsents$intentId: customerId = $customerId")
            val retrieveConsents = async {
                customerId?.takeIf { needRequestConsent() }
                    ?.let { retrieveAvailablePaymentConsents(secret, it) }
                    ?: emptyList()
            }
            val retrieveMethods = async { retrieveAvailablePaymentMethods(secret) }
            try {
                val methods = filterPaymentMethodsBySession(
                    retrieveMethods.await(),
                    session.paymentMethods
                )
                val consents = retrieveConsents.await()
                Result.success(Pair(methods, filterPaymentConsentsBySession(methods, consents)))
            } catch (exception: AirwallexException) {
                AirwallexLogger.error(
                    "PaymentMethodsViewModel fetchAvailablePaymentMethodsAndConsents$intentId: failed ",
                    exception
                )
                Result.failure(exception)
            }
        }
    }

    private fun requireHandleSchemaFields(paymentMethodType: AvailablePaymentMethodType) =
        paymentMethodType.resources?.hasSchema == true

    private fun needRequestConsent(): Boolean {
        // if the customerId is null or empty ,there is no need to request consents
        if (customerId.isNullOrEmpty()) return false
        // only payment mode needs to request consents
        if (session !is AirwallexPaymentSession) return false
        // if user wants to hide consents,there is no need to request consents
        return !hidePaymentConsents
    }

    private fun filterPaymentMethodsBySession(
        sourceList: List<AvailablePaymentMethodType>,
        filterList: List<String>?
    ): List<AvailablePaymentMethodType> {
        if (filterList.isNullOrEmpty()) return sourceList
        return filterList.mapNotNull { name ->
            sourceList.find { it.name.equals(name, ignoreCase = true) }
        }
    }

    private fun filterPaymentConsentsBySession(
        paymentMethodList: List<AvailablePaymentMethodType>,
        paymentConsentList: List<PaymentConsent>
    ): List<PaymentConsent> {
        val cardPaymentMethod = paymentMethodList.findWithType(PaymentMethodType.CARD)
        return if (cardPaymentMethod != null && session is AirwallexPaymentSession) {
            paymentConsentList.filter { it.paymentMethod?.type == PaymentMethodType.CARD.value }
        } else {
            emptyList()
        }
    }

    private suspend fun retrieveAvailablePaymentConsents(
        clientSecret: String,
        customerId: String
    ) = loadPagedItems(
        loadPage = { pageNum ->
            airwallex.retrieveAvailablePaymentConsents(
                RetrieveAvailablePaymentConsentsParams.Builder(
                    clientSecret = clientSecret,
                    customerId = customerId,
                    pageNum = pageNum
                )
                    .setNextTriggeredBy(PaymentConsent.NextTriggeredBy.CUSTOMER)
                    .setStatus(PaymentConsent.PaymentConsentStatus.VERIFIED)
                    .build()
            )
        }
    )

    private suspend fun retrieveAvailablePaymentMethods(
        clientSecret: String
    ) = loadPagedItems(
        loadPage = { pageNum ->
            airwallex.retrieveAvailablePaymentMethods(
                session = session,
                params = RetrieveAvailablePaymentMethodParams.Builder(
                    clientSecret = clientSecret,
                    pageNum = pageNum
                )
                    .setActive(true)
                    .setTransactionCurrency(session.currency)
                    .setCountryCode(session.countryCode)
                    .build()
            )
        }
    )

    private suspend fun <T> loadPagedItems(
        loadPage: suspend (Int) -> Page<T>,
        items: MutableList<T> = Collections.synchronizedList(mutableListOf()),
        pageNum: AtomicInteger = AtomicInteger(0)
    ): List<T> {
        val response = loadPage(pageNum.get())
        pageNum.incrementAndGet()
        items.addAll(response.items)
        return if (response.hasMore) {
            loadPagedItems(
                loadPage,
                items,
                pageNum
            )
        } else {
            items
        }
    }

    fun deletePaymentConsent(paymentConsent: PaymentConsent): LiveData<Result<PaymentConsent>> {
        val resultData = MutableLiveData<Result<PaymentConsent>>()
        try {
            clientSecret?.let {
                airwallex.disablePaymentConsent(
                    DisablePaymentConsentParams(
                        clientSecret = it,
                        paymentConsentId = requireNotNull(paymentConsent.id),
                    ),
                    object : Airwallex.PaymentListener<PaymentConsent> {

                        override fun onFailed(exception: AirwallexException) {
                            resultData.value = Result.failure(exception)
                        }

                        override fun onSuccess(response: PaymentConsent) {
                            resultData.value = Result.success(paymentConsent)
                        }
                    }
                )
            } ?: {
                resultData.value =
                    Result.failure(AirwallexCheckoutException(message = "clientSecret is null"))
            }
        } catch (e: AirwallexCheckoutException) {
            resultData.value = Result.failure(e)
        }
        return resultData
    }

    fun retrieveSchemaDataFromCache(paymentMethodType: AvailablePaymentMethodType): SchemaData? {
        return schemaDataCache[paymentMethodType]
    }

    suspend fun loadSchemaFields(paymentMethodType: AvailablePaymentMethodType): SchemaData? {
        // Return cached data if available
        schemaDataCache[paymentMethodType]?.let { return it }

        AirwallexLogger.info("PaymentMethodsViewModel loadSchemaFields, type = ${paymentMethodType.name}")
        val paymentMethod = PaymentMethod.Builder()
            .setType(paymentMethodType.name)
            .build()
        paymentMethod.type?.let { type ->
            if (requireHandleSchemaFields(paymentMethodType)) { // Have required schema fields
                AirwallexLogger.info("PaymentMethodsViewModel get more payment Info fields on one-off flow.")
                // 1.Retrieve all required schema fields of the payment method
                val typeInfo = retrievePaymentMethodTypeInfo(type).getOrElse { exception ->
                    _paymentFlowStatus.value = PaymentFlowStatus.ErrorAlert(exception.localizedMessage ?: "")
                    schemaDataCache[paymentMethodType] = SchemaData()
                    return null
                }
                // Ad hoc. If country code is required, use session's country code.
                // Other fields like os_type and flow will not need to be retrieved here.
                if (typeInfo.needCountryCode(transactionMode)) {
                    additionalParams[COUNTRY_CODE] = session.countryCode
                }
                val fields = typeInfo.filterRequiredFields(transactionMode) ?: return null
                AirwallexLogger.info("PaymentMethodsViewModel loadSchemaFields: filterRequiredFields = $fields")
                // 2.If all fields are hidden, start checkout directly
                if (fields.isEmpty()) {
                    schemaDataCache[paymentMethodType] = SchemaData()
                    return SchemaData()
                }
                val bankField = fields.find { field -> field.type == DynamicSchemaFieldType.BANKS }
                AirwallexLogger.info("PaymentMethodsViewModel loadSchemaFields: bankField = $bankField")
                if (bankField == null) {
                    return SchemaData(
                        fields = fields,
                        paymentMethod = paymentMethod,
                        typeInfo = typeInfo,
                    ).also { schemaData ->
                        schemaDataCache[paymentMethodType] = schemaData
                    }
                }
                // 3.If the bank is needed, need to retrieve the bank list.
                val banks = retrieveBanks(type).getOrElse { exception ->
                    _paymentFlowStatus.value = PaymentFlowStatus.ErrorAlert(exception.localizedMessage ?: "")
                    schemaDataCache[paymentMethodType] = SchemaData()
                    return null
                }.items
                AirwallexLogger.info("PaymentMethodsViewModel loadSchemaFields: banks = $banks")
                // 4.If the bank is not needed or bank list is empty, then show the schema fields.
                if (banks.isNullOrEmpty()) {
                    return SchemaData(
                        fields = fields,
                        paymentMethod = paymentMethod,
                        typeInfo = typeInfo,
                    ).also { schemaData ->
                        schemaDataCache[paymentMethodType] = schemaData
                    }
                } else {
                    return SchemaData(
                        fields = fields,
                        paymentMethod = paymentMethod,
                        typeInfo = typeInfo,
                        banks = banks,
                    ).also { schemaData ->
                        schemaDataCache[paymentMethodType] = schemaData
                    }
                }
            }
        }
        // Default to null
        return null
    }

    fun appendParamsToMapForSchemaSubmission(map: Map<String, String>): Map<String, String> {
        return map + additionalParams
    }

    internal class Factory(
        private val application: Application,
        private val airwallex: Airwallex,
        private val session: AirwallexSession
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PaymentMethodsViewModel(
                application,
                airwallex,
                session
            ) as T
        }
    }

    internal sealed class PaymentMethodResult {
        data class Show(val methods: Pair<List<AvailablePaymentMethodType>, List<PaymentConsent>>) : PaymentMethodResult()
        data class Skip(val schemes: List<CardScheme>) : PaymentMethodResult()
    }

    internal sealed class PaymentFlowStatus {
        data class ErrorAlert(
            val message: String,
        ) : PaymentFlowStatus()

        data class PaymentStatus(
            val status: AirwallexPaymentStatus,
        ) : PaymentFlowStatus()
    }

    data class SchemaData(
        val fields: List<DynamicSchemaField> = emptyList(),
        val paymentMethod: PaymentMethod? = null,
        val typeInfo: PaymentMethodTypeInfo? = null,
        val banks: List<Bank> = emptyList(),
    )

    companion object {
        const val COUNTRY_CODE = "country_code"
        const val FLOW = "flow"
        private const val PAYMENT_METHOD = "payment_method"
        private const val PAYMENT_SUCCESS = "payment_success"
        private const val PAYMENT_SELECT = "select_payment"
    }
}
