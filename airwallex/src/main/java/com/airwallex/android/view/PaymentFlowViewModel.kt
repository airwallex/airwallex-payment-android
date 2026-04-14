package com.airwallex.android.view

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.Airwallex.PaymentResultListener
import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.AirwallexPaymentRequestFlow
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.Billing
import com.airwallex.android.core.model.DisablePaymentConsentParams
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethod
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * ViewModel for managing payment operations data.
 * Handles fetching and storing payment methods and consents.
 */
class PaymentFlowViewModel(
    private val airwallex: Airwallex,
    private val session: AirwallexSession
) : ViewModel() {

    private val _availablePaymentMethods = MutableStateFlow<List<AvailablePaymentMethodType>>(emptyList())
    val availablePaymentMethods: StateFlow<List<AvailablePaymentMethodType>> = _availablePaymentMethods.asStateFlow()

    // Store original list of consents
    private val _originalPaymentConsents = MutableStateFlow<List<PaymentConsent>>(emptyList())

    // Deduplicated list for display
    private val _availablePaymentConsents = MutableStateFlow<List<PaymentConsent>>(emptyList())
    val availablePaymentConsents: StateFlow<List<PaymentConsent>> = _availablePaymentConsents.asStateFlow()

    // Payment flow result event - one-time event stream
    private val _paymentResult = Channel<PaymentResultEvent>(capacity = Channel.CONFLATED)
    val paymentResult: Flow<PaymentResultEvent> = _paymentResult.receiveAsFlow()

    // Delete payment consent result - one-time event stream
    private val _deleteConsentResult = MutableSharedFlow<DeleteConsentResult>(replay = 0)
    val deleteConsentResult: SharedFlow<DeleteConsentResult> = _deleteConsentResult.asSharedFlow()

    /**
     * Event that wraps payment status with the flow type that triggered it
     */
    data class PaymentResultEvent(
        val flowType: PaymentFlowType,
        val status: AirwallexPaymentStatus
    )

    /**
     * Result of deleting a payment consent
     */
    sealed class DeleteConsentResult {
        data class Success(val consent: PaymentConsent) : DeleteConsentResult()
        data class Failure(val exception: Throwable) : DeleteConsentResult()
    }

    enum class PaymentFlowType {
        CHECKOUT_WITH_NEW_CARD,
        CHECKOUT_WITH_CVC,
        CHECKOUT_WITHOUT_CVC,
        CHECKOUT_WITH_GOOGLE_PAY
    }

    fun updateActivity(newActivity: ComponentActivity) {
        airwallex.updateActivity(newActivity)
    }

    /**
     * Fetches available payment methods and consents.
     * Should only be called once from the parent component.
     */
    suspend fun fetchAvailablePaymentMethodsAndConsents(): Result<Pair<List<AvailablePaymentMethodType>, List<PaymentConsent>>> {
        val result = airwallex.fetchAvailablePaymentMethodsAndConsents(session)

        result.fold(
            onSuccess = { (methods, consents) ->
                _availablePaymentMethods.value = methods
                _originalPaymentConsents.value = consents
                _availablePaymentConsents.value = deduplicateConsents(consents)
            },
            onFailure = { _ ->
                // nothing, we return the entire result
            }
        )

        return result
    }

    /**
     * Deduplicates payment consents by fingerprint with the following rules:
     * 1. If CIT and MIT share the same fingerprint, prioritize the CIT consent (only 1 CIT per fingerprint)
     * 2. When multiple MIT consents exist with the same fingerprint, keep the first one
     */
    private fun deduplicateConsents(consents: List<PaymentConsent>): List<PaymentConsent> {
        val grouped = consents.groupBy { it.paymentMethod?.card?.fingerprint }

        return grouped.flatMap { (fingerprint, consentsGroup) ->
            // If fingerprint is null, discard (indicates invalid consent data)
            if (fingerprint == null) {
                return@flatMap emptyList()
            }

            // Find CIT consent (only 1 per fingerprint)
            val citConsent = consentsGroup.find {
                it.nextTriggeredBy == PaymentConsent.NextTriggeredBy.CUSTOMER
            }

            // If there's a CIT consent, prioritize it and ignore MIT
            if (citConsent != null) {
                listOf(citConsent)
            } else {
                // If only MIT consents exist, keep the first one
                val mitConsent = consentsGroup.find {
                    it.nextTriggeredBy == PaymentConsent.NextTriggeredBy.MERCHANT
                }
                listOfNotNull(mitConsent)
            }
        }
    }

    fun deletePaymentConsent(paymentConsent: PaymentConsent) = viewModelScope.launch {
        val clientSecret = airwallex.getClientSecret(session)
        if (clientSecret == null) {
            _deleteConsentResult.emit(
                DeleteConsentResult.Failure(
                    AirwallexCheckoutException(message = "clientSecret is null")
                )
            )
            return@launch
        }

        suspendCancellableCoroutine { continuation ->
            airwallex.disablePaymentConsent(
                DisablePaymentConsentParams(
                    clientSecret = clientSecret,
                    paymentConsentId = requireNotNull(paymentConsent.id),
                ),
                object : Airwallex.PaymentListener<PaymentConsent> {
                    override fun onFailed(exception: AirwallexException) {
                        viewModelScope.launch {
                            _deleteConsentResult.emit(DeleteConsentResult.Failure(exception))
                        }
                        continuation.resume(Unit)
                    }

                    override fun onSuccess(response: PaymentConsent) {
                        viewModelScope.launch {
                            // Remove from original list
                            val updatedOriginalList = _originalPaymentConsents.value.filter { it.id != paymentConsent.id }
                            _originalPaymentConsents.value = updatedOriginalList

                            // Re-deduplicate to potentially show MIT if CIT was deleted
                            _availablePaymentConsents.value = deduplicateConsents(updatedOriginalList)

                            _deleteConsentResult.emit(DeleteConsentResult.Success(paymentConsent))
                        }
                        continuation.resume(Unit)
                    }
                },
            )
        }
    }

    fun confirmPaymentIntent(paymentConsent: PaymentConsent) {
        viewModelScope.launch {
            if (session !is AirwallexPaymentSession) {
                _paymentResult.send(
                    PaymentResultEvent(
                        flowType = PaymentFlowType.CHECKOUT_WITHOUT_CVC,
                        status = AirwallexPaymentStatus.Failure(
                            AirwallexCheckoutException(message = "confirm with paymentConsent only support AirwallexPaymentSession")
                        )
                    )
                )
                return@launch
            }

            airwallex.confirmPaymentIntent(
                session,
                paymentConsent,
                object : PaymentResultListener {
                    override fun onCompleted(status: AirwallexPaymentStatus) {
                        viewModelScope.launch {
                            _paymentResult.send(
                                PaymentResultEvent(
                                    flowType = PaymentFlowType.CHECKOUT_WITHOUT_CVC,
                                    status = status
                                )
                            )
                        }
                    }
                }
            )
        }
    }

    fun checkoutWithCvc(
        paymentConsent: PaymentConsent,
        cvc: String
    ) = viewModelScope.launch {
        val paymentMethod = paymentConsent.paymentMethod
        if (paymentMethod == null) {
            _paymentResult.send(
                PaymentResultEvent(
                    flowType = PaymentFlowType.CHECKOUT_WITH_CVC,
                    status = AirwallexPaymentStatus.Failure(
                        AirwallexCheckoutException(message = "checkout with paymentConsent without paymentMethod")
                    )
                )
            )
            return@launch
        }
        if (session !is AirwallexPaymentSession) {
            _paymentResult.send(
                PaymentResultEvent(
                    flowType = PaymentFlowType.CHECKOUT_WITH_CVC,
                    status = AirwallexPaymentStatus.Failure(
                        AirwallexCheckoutException(message = "checkout with paymentConsent only support AirwallexPaymentSession")
                    )
                )
            )
            return@launch
        }

        val status = checkout(
            paymentMethod = paymentMethod,
            paymentConsentId = paymentConsent.id,
            cvc = cvc,
        )
        _paymentResult.send(
            PaymentResultEvent(
                flowType = PaymentFlowType.CHECKOUT_WITH_CVC,
                status = status
            )
        )
    }

    fun checkoutWithGooglePay() = viewModelScope.launch {
        val status = checkoutGooglePay()
        _paymentResult.send(
            PaymentResultEvent(
                flowType = PaymentFlowType.CHECKOUT_WITH_GOOGLE_PAY,
                status = status
            )
        )
    }

    fun checkoutWithNewCard(
        card: PaymentMethod.Card,
        saveCard: Boolean,
        billing: Billing?
    ) = viewModelScope.launch {
        val status = suspendCancellableCoroutine { continuation ->
            airwallex.confirmPaymentIntent(
                session = session,
                card = card,
                billing = billing,
                saveCard = saveCard,
                listener = object : PaymentResultListener {
                    override fun onCompleted(status: AirwallexPaymentStatus) {
                        continuation.resume(status)
                    }
                }
            )
        }
        _paymentResult.send(
            PaymentResultEvent(
                flowType = PaymentFlowType.CHECKOUT_WITH_NEW_CARD,
                status = status
            )
        )
    }

    fun trackScreenViewed(eventName: String, params: Map<String, Any> = emptyMap()) {
        AnalyticsLogger.logPaymentView(
            viewName = eventName,
            additionalInfo = params,
        )
    }

    private suspend fun checkout(
        paymentMethod: PaymentMethod,
        paymentConsentId: String?,
        cvc: String,
        flow: AirwallexPaymentRequestFlow = AirwallexPaymentRequestFlow.IN_APP,
    ): AirwallexPaymentStatus {
        return suspendCancellableCoroutine { continuation ->
            airwallex.checkout(
                session = session,
                paymentMethod = paymentMethod,
                paymentConsentId = paymentConsentId,
                cvc = cvc,
                flow = flow,
                listener = object : PaymentResultListener {
                    override fun onCompleted(status: AirwallexPaymentStatus) {
                        continuation.resume(status)
                    }
                }
            )
        }
    }

    private suspend fun checkoutGooglePay(): AirwallexPaymentStatus {
        return suspendCancellableCoroutine { continuation ->
            airwallex.startGooglePay(
                session = session,
                listener = object : PaymentResultListener {
                    override fun onCompleted(status: AirwallexPaymentStatus) {
                        continuation.resume(status)
                    }
                }
            )
        }
    }

    class Factory(
        private val airwallex: Airwallex,
        private val session: AirwallexSession
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PaymentFlowViewModel(airwallex, session) as T
        }
    }

    companion object {
        const val COUNTRY_CODE = "country_code"
        const val FLOW = "flow"
    }
}