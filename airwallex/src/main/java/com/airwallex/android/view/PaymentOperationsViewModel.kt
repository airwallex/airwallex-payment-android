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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * ViewModel for managing payment operations data.
 * Handles fetching and storing payment methods and consents.
 */
class PaymentOperationsViewModel(
    private val airwallex: Airwallex,
    private val session: AirwallexSession
) : ViewModel() {

    private val _availablePaymentMethods = MutableStateFlow<List<AvailablePaymentMethodType>>(emptyList())
    val availablePaymentMethods: StateFlow<List<AvailablePaymentMethodType>> = _availablePaymentMethods.asStateFlow()

    private val _availablePaymentConsents = MutableStateFlow<List<PaymentConsent>>(emptyList())
    val availablePaymentConsents: StateFlow<List<PaymentConsent>> = _availablePaymentConsents.asStateFlow()

    // Payment operation result event - one-time event stream
    private val _paymentResult = MutableSharedFlow<PaymentResultEvent>(replay = 0)
    val paymentResult: SharedFlow<PaymentResultEvent> = _paymentResult.asSharedFlow()

    // Delete payment consent result - one-time event stream
    private val _deleteConsentResult = MutableSharedFlow<DeleteConsentResult>(replay = 0)
    val deleteConsentResult: SharedFlow<DeleteConsentResult> = _deleteConsentResult.asSharedFlow()

    /**
     * Event that wraps payment status with the operation type that triggered it
     */
    data class PaymentResultEvent(
        val operationType: PaymentOperationType,
        val status: AirwallexPaymentStatus
    )

    /**
     * Result of deleting a payment consent
     */
    sealed class DeleteConsentResult {
        data class Success(val consent: PaymentConsent) : DeleteConsentResult()
        data class Failure(val exception: Throwable) : DeleteConsentResult()
    }

    enum class PaymentOperationType {
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
                _availablePaymentConsents.value = consents
            },
            onFailure = { _ ->
                // nothing, we return the entire result
            }
        )

        return result
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
                _paymentResult.emit(
                    PaymentResultEvent(
                        operationType = PaymentOperationType.CHECKOUT_WITHOUT_CVC,
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
                            _paymentResult.emit(
                                PaymentResultEvent(
                                    operationType = PaymentOperationType.CHECKOUT_WITHOUT_CVC,
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
            _paymentResult.emit(
                PaymentResultEvent(
                    operationType = PaymentOperationType.CHECKOUT_WITH_CVC,
                    status = AirwallexPaymentStatus.Failure(
                        AirwallexCheckoutException(message = "checkout with paymentConsent without paymentMethod")
                    )
                )
            )
            return@launch
        }
        if (session !is AirwallexPaymentSession) {
            _paymentResult.emit(
                PaymentResultEvent(
                    operationType = PaymentOperationType.CHECKOUT_WITH_CVC,
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
        _paymentResult.emit(
            PaymentResultEvent(
                operationType = PaymentOperationType.CHECKOUT_WITH_CVC,
                status = status
            )
        )
    }

    fun checkoutWithGooglePay() = viewModelScope.launch {
        val status = checkoutGooglePay()
        _paymentResult.emit(
            PaymentResultEvent(
                operationType = PaymentOperationType.CHECKOUT_WITH_GOOGLE_PAY,
                status = status
            )
        )
    }

    fun checkoutWithNewCard(
        card: PaymentMethod.Card, saveCard: Boolean, billing: Billing?
    ) = viewModelScope.launch {
        if (session !is AirwallexPaymentSession) {
            _paymentResult.emit(
                PaymentResultEvent(
                    operationType = PaymentOperationType.CHECKOUT_WITH_NEW_CARD,
                    status = AirwallexPaymentStatus.Failure(
                        AirwallexCheckoutException(message = "checkout with new card only supports AirwallexPaymentSession")
                    )
                )
            )
            return@launch
        }
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
        _paymentResult.emit(
            PaymentResultEvent(
                operationType = PaymentOperationType.CHECKOUT_WITH_NEW_CARD,
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
            return PaymentOperationsViewModel(airwallex, session) as T
        }
    }
}