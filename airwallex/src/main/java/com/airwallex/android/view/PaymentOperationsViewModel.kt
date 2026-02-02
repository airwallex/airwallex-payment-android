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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Fetches available payment methods and consents.
     * Should only be called once from the parent component.
     */
    suspend fun fetchAvailablePaymentMethodsAndConsents(): Result<Pair<List<AvailablePaymentMethodType>, List<PaymentConsent>>> {
        _isLoading.value = true

        val result = airwallex.fetchAvailablePaymentMethodsAndConsents(session)

        result.fold(
            onSuccess = { (methods, consents) ->
                _availablePaymentMethods.value = methods
                _availablePaymentConsents.value = consents
                _isLoading.value = false
            },
            onFailure = { _ ->
                _isLoading.value = false
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

    fun confirmPaymentIntent(
        paymentConsent: PaymentConsent,
        onOperationDone: (AirwallexPaymentStatus) -> Unit
    ) {
        if (session !is AirwallexPaymentSession) {
            onOperationDone(
                AirwallexPaymentStatus.Failure(
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
                    onOperationDone(status)
                }
            }
        )
    }

    fun checkoutWithCvc(
        paymentConsent: PaymentConsent,
        cvc: String,
        onOperationDone: (AirwallexPaymentStatus) -> Unit
    )  = viewModelScope.launch {
        val paymentMethod = paymentConsent.paymentMethod
        if (paymentMethod == null) {
            onOperationDone(
                AirwallexPaymentStatus.Failure(
                    AirwallexCheckoutException(message = "checkout with paymentConsent without paymentMethod")
                )
            )
            return@launch
        }
        if (session !is AirwallexPaymentSession) {
            onOperationDone(
                AirwallexPaymentStatus.Failure(
                    AirwallexCheckoutException(message = "checkout with paymentConsent only support AirwallexPaymentSession")
                )
            )
            return@launch
        }

        checkout(
            paymentMethod = paymentMethod,
            paymentConsentId = paymentConsent.id,
            cvc = cvc,
        ).let { status ->
            onOperationDone(status)
        }
    }

    fun checkoutWithGooglePay(
        onOperationDone: (AirwallexPaymentStatus) -> Unit
    ) = viewModelScope.launch {
        val status = checkoutGooglePay()
        onOperationDone(status)
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
                listener = object : Airwallex.PaymentResultListener {
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
                listener = object : Airwallex.PaymentResultListener {
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
