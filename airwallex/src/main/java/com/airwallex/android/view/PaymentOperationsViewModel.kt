package com.airwallex.android.view

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
import com.airwallex.android.core.model.DisablePaymentConsentParams
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    // Payment operation result event with operation type tracking
    private val _paymentResult = MutableStateFlow<PaymentResultEvent?>(null)
    val paymentResult: StateFlow<PaymentResultEvent?> = _paymentResult.asStateFlow()

    /**
     * Event that wraps payment status with the operation type that triggered it
     */
    data class PaymentResultEvent(
        val operationType: PaymentOperationType,
        val status: AirwallexPaymentStatus
    )

    enum class PaymentOperationType {
        CHECKOUT_WITH_CVC,
        CHECKOUT_WITHOUT_CVC,
        CHECKOUT_WITH_GOOGLE_PAY
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

    suspend fun deletePaymentConsent(
        paymentConsent: PaymentConsent
    ): Result<Unit> {
        val clientSecret = airwallex.getClientSecret(session)
            ?: return Result.failure(AirwallexCheckoutException(message = "clientSecret is null"))

        return suspendCancellableCoroutine { continuation ->
            airwallex.disablePaymentConsent(
                DisablePaymentConsentParams(
                    clientSecret = clientSecret,
                    paymentConsentId = requireNotNull(paymentConsent.id),
                ),
                object : Airwallex.PaymentListener<PaymentConsent> {
                    override fun onFailed(exception: AirwallexException) {
                        continuation.resume(Result.failure(exception))
                    }

                    override fun onSuccess(response: PaymentConsent) {
                        continuation.resume(Result.success(Unit))
                    }
                },
            )
        }
    }

    fun confirmPaymentIntent(paymentConsent: PaymentConsent) {
        if (session !is AirwallexPaymentSession) {
            _paymentResult.value = PaymentResultEvent(
                operationType = PaymentOperationType.CHECKOUT_WITHOUT_CVC,
                status = AirwallexPaymentStatus.Failure(
                    AirwallexCheckoutException(message = "confirm with paymentConsent only support AirwallexPaymentSession")
                )
            )
            return
        }

        airwallex.confirmPaymentIntent(
            session,
            paymentConsent,
            object : PaymentResultListener {
                override fun onCompleted(status: AirwallexPaymentStatus) {
                    _paymentResult.value = PaymentResultEvent(
                        operationType = PaymentOperationType.CHECKOUT_WITHOUT_CVC,
                        status = status
                    )
                }
            }
        )
    }

    fun checkoutWithCvc(
        paymentConsent: PaymentConsent,
        cvc: String
    ) = viewModelScope.launch {
        val paymentMethod = paymentConsent.paymentMethod
        if (paymentMethod == null) {
            _paymentResult.value = PaymentResultEvent(
                operationType = PaymentOperationType.CHECKOUT_WITH_CVC,
                status = AirwallexPaymentStatus.Failure(
                    AirwallexCheckoutException(message = "checkout with paymentConsent without paymentMethod")
                )
            )
            return@launch
        }
        if (session !is AirwallexPaymentSession) {
            _paymentResult.value = PaymentResultEvent(
                operationType = PaymentOperationType.CHECKOUT_WITH_CVC,
                status = AirwallexPaymentStatus.Failure(
                    AirwallexCheckoutException(message = "checkout with paymentConsent only support AirwallexPaymentSession")
                )
            )
            return@launch
        }

        val status = checkout(
            paymentMethod = paymentMethod,
            paymentConsentId = paymentConsent.id,
            cvc = cvc,
        )
        _paymentResult.value = PaymentResultEvent(
            operationType = PaymentOperationType.CHECKOUT_WITH_CVC,
            status = status
        )
    }

    fun checkoutWithGooglePay() = viewModelScope.launch {
        val status = checkoutGooglePay()
        _paymentResult.value = PaymentResultEvent(
            operationType = PaymentOperationType.CHECKOUT_WITH_GOOGLE_PAY,
            status = status
        )
    }

    fun clearPaymentResult() {
        _paymentResult.value = null
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
