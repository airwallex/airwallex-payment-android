package com.airwallex.android.view

import android.app.Application
import androidx.lifecycle.*
import com.airwallex.android.core.*
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.extension.putIfNotNull
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.*
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

internal class PaymentMethodsViewModel(
    application: Application,
    private val airwallex: Airwallex,
    private val session: AirwallexSession
) : AndroidViewModel(application) {

    val pageName: String = "payment_method_list"

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

    fun trackCardPaymentSuccess() {
        AnalyticsLogger.logAction(
            PAYMENT_SUCCESS,
            mapOf(PAYMENT_METHOD to PaymentMethodType.CARD.value)
        )
    }

    fun trackPaymentSuccess(paymentConsent: PaymentConsent) {
        AnalyticsLogger.logAction(
            PAYMENT_SUCCESS,
            mutableMapOf<String, String>().apply {
                putIfNotNull(PAYMENT_METHOD, paymentConsent.paymentMethod?.type)
            }
        )
    }

    fun trackCardPaymentSelection() {
        AnalyticsLogger.logAction(
            PAYMENT_SELECT,
            mapOf(PAYMENT_METHOD to PaymentMethodType.CARD.value)
        )
    }

    fun trackPaymentSelection(paymentConsent: PaymentConsent) {
        paymentConsent.paymentMethod?.type?.takeIf { it.isNotEmpty() }?.let { type ->
            AnalyticsLogger.logAction(PAYMENT_SELECT, mapOf(PAYMENT_METHOD to type))
        }
    }

    fun filterRequiredFields(info: PaymentMethodTypeInfo): List<DynamicSchemaField>? {
        return info
            .fieldSchemas
            ?.firstOrNull { schema -> schema.transactionMode == TransactionMode.ONE_OFF }
            ?.fields
            ?.filter { !it.hidden }
    }

    fun fetchPaymentFlow(info: PaymentMethodTypeInfo): AirwallexPaymentRequestFlow {
        val flowField = info
            .fieldSchemas
            ?.firstOrNull { schema -> schema.transactionMode == TransactionMode.ONE_OFF }
            ?.fields
            ?.firstOrNull { it.name == FLOW }

        val candidates = flowField?.candidates
        return when {
            candidates?.find { it.value == AirwallexPaymentRequestFlow.IN_APP.value } != null -> {
                AirwallexPaymentRequestFlow.IN_APP
            }
            candidates != null && candidates.isNotEmpty() -> {
                AirwallexPaymentRequestFlow.fromValue(candidates[0].value)
                    ?: AirwallexPaymentRequestFlow.IN_APP
            }
            else -> {
                AirwallexPaymentRequestFlow.IN_APP
            }
        }
    }

    suspend fun fetchAvailablePaymentMethodsAndConsents():
            Result<Pair<List<AvailablePaymentMethodType>, List<PaymentConsent>>>? {
        return when (session) {
            is AirwallexPaymentSession, is AirwallexRecurringWithIntentSession -> {
                paymentIntent?.clientSecret
            }
            is AirwallexRecurringSession -> {
                try {
                    ClientSecretRepository.getInstance()
                        .retrieveClientSecret(requireNotNull(session.customerId))
                        .value
                } catch (e: AirwallexCheckoutException) {
                    return Result.failure(e)
                }
            }
            else -> null
        }?.let { clientSecret ->
            TokenManager.updateClientSecret(clientSecret)
            supervisorScope {
                val retrieveConsents = async {
                    customerId?.let {
                        retrieveAvailablePaymentConsents(clientSecret, it)
                    } ?: emptyList()
                }
                val retrieveMethods = async { retrieveAvailablePaymentMethods(clientSecret) }
                try {
                    Result.success(Pair(retrieveMethods.await(), retrieveConsents.await()))
                } catch (exception: AirwallexException) {
                    Result.failure(exception)
                }
            }
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
            ClientSecretRepository.getInstance().retrieveClientSecret(
                requireNotNull(session.customerId),
                object : ClientSecretRepository.ClientSecretRetrieveListener {
                    override fun onClientSecretRetrieve(clientSecret: ClientSecret) {
                        airwallex.disablePaymentConsent(
                            DisablePaymentConsentParams(
                                clientSecret = clientSecret.value,
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
                    }

                    override fun onClientSecretError(errorMessage: String) {
                        resultData.value =
                            Result.failure(AirwallexCheckoutException(message = errorMessage))
                    }
                }
            )
        } catch (e: AirwallexCheckoutException) {
            resultData.value = Result.failure(e)
        }
        return resultData
    }

    fun hasSinglePaymentMethod(
        desiredPaymentMethodType: AvailablePaymentMethodType?,
        paymentMethods: List<AvailablePaymentMethodType>,
        consents: List<PaymentConsent>
    ): Boolean {
        if (desiredPaymentMethodType == null) return false

        val hasPaymentConsents = consents.isNotEmpty()
        val availablePaymentMethodsSize = paymentMethods.size

        return !hasPaymentConsents && availablePaymentMethodsSize == 1
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

    companion object {
        const val COUNTRY_CODE = "country_code"
        const val FLOW = "flow"
        private const val PAYMENT_METHOD = "payment_method"
        private const val PAYMENT_SUCCESS = "payment_success"
        private const val PAYMENT_SELECT = "select_payment"
    }
}
