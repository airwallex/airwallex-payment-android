package com.airwallex.android.ui.checkout

import android.app.Application
import androidx.lifecycle.*
import com.airwallex.android.core.*
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

open class AirwallexCheckoutViewModel(
    application: Application,
    private val airwallex: Airwallex,
    private val session: AirwallexSession
) : AndroidViewModel(application) {

    companion object {
        private const val EVENT_PAYMENT_CANCELLED = "payment_canceled"
        private const val EVENT_PAYMENT_LAUNCHED = "payment_launched"
        private const val KEY_PAYMENT_INTENT_ID = "payment_intent_id"
        private const val KEY_TRANSACTION_MODE = "transaction_mode"
    }

    private val additionalAnalyticsEventParams: Map<String, String> by lazy {
        when (session) {
            is AirwallexPaymentSession -> {
                mapOf(
                    KEY_PAYMENT_INTENT_ID to session.paymentIntent.id,
                    KEY_TRANSACTION_MODE to TransactionMode.ONE_OFF.value,
                )
            }

            is AirwallexRecurringSession -> {
                mapOf(
                    KEY_TRANSACTION_MODE to TransactionMode.RECURRING.value,
                )
            }

            is AirwallexRecurringWithIntentSession -> {
                mapOf(
                    KEY_PAYMENT_INTENT_ID to session.paymentIntent.id,
                    KEY_TRANSACTION_MODE to TransactionMode.RECURRING.value,
                )
            }

            else -> mapOf()
        }
    }

    val transactionMode: TransactionMode by lazy {
        when (session) {
            is AirwallexRecurringSession, is AirwallexRecurringWithIntentSession -> TransactionMode.RECURRING
            is AirwallexPaymentSession -> TransactionMode.ONE_OFF
            else -> TransactionMode.ONE_OFF // Default to one-off if session is unavailable
        }
    }

    @Suppress("LongParameterList")
    fun checkout(
        paymentMethod: PaymentMethod,
        paymentConsentId: String?,
        cvc: String?,
        additionalInfo: Map<String, String>? = null,
        flow: AirwallexPaymentRequestFlow? = null
    ): LiveData<AirwallexPaymentStatus> {
        val resultData = MutableLiveData<AirwallexPaymentStatus>()
        val listener = object : Airwallex.PaymentResultListener {
            override fun onCompleted(status: AirwallexPaymentStatus) {
                resultData.value = status
            }
        }
        airwallex.checkout(
            session,
            paymentMethod,
            paymentConsentId,
            cvc,
            additionalInfo,
            flow,
            listener
        )

        return resultData
    }

    suspend fun checkout(
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

    suspend fun checkout(
        paymentMethod: PaymentMethod,
        additionalInfo: Map<String, String>? = null,
        flow: AirwallexPaymentRequestFlow? = null
    ): AirwallexPaymentStatus {
        return suspendCancellableCoroutine { continuation ->
            airwallex.checkout(
                session = session,
                paymentMethod = paymentMethod,
                additionalInfo = additionalInfo,
                flow = flow,
                listener = object : Airwallex.PaymentResultListener {
                    override fun onCompleted(status: AirwallexPaymentStatus) {
                        continuation.resume(status)
                    }
                }
            )
        }
    }

    suspend fun checkoutGooglePay(): AirwallexPaymentStatus {
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

    suspend fun retrieveBanks(paymentMethodTypeName: String): Result<BankResponse> {
        return suspendCancellableCoroutine { continuation ->
            when (session) {
                is AirwallexPaymentSession -> {
                    val paymentIntent = session.paymentIntent
                    airwallex.retrieveBanks(
                        RetrieveBankParams.Builder(
                            clientSecret = requireNotNull(paymentIntent.clientSecret),
                            paymentMethodType = paymentMethodTypeName
                        )
                            .setCountryCode(session.countryCode)
                            .build(),
                        object : Airwallex.PaymentListener<BankResponse> {
                            override fun onFailed(exception: AirwallexException) {
                                continuation.resume(Result.failure(exception))
                            }

                            override fun onSuccess(response: BankResponse) {
                                continuation.resume(Result.success(response))
                            }
                        }
                    )
                }

                else -> {
                    continuation.resume(
                        Result.failure(AirwallexCheckoutException(message = "$paymentMethodTypeName just support one-off payment"))
                    )
                }
            }
        }
    }

    suspend fun retrievePaymentMethodTypeInfo(
        paymentMethodTypeName: String
    ): Result<PaymentMethodTypeInfo> {
        return suspendCancellableCoroutine { continuation ->
            when (session) {
                is AirwallexPaymentSession -> {
                    val paymentIntent = session.paymentIntent
                    airwallex.retrievePaymentMethodTypeInfo(
                        RetrievePaymentMethodTypeInfoParams.Builder(
                            clientSecret = requireNotNull(paymentIntent.clientSecret),
                            paymentMethodType = paymentMethodTypeName
                        )
                            .setFlow(AirwallexPaymentRequestFlow.IN_APP)
                            .build(),
                        object : Airwallex.PaymentListener<PaymentMethodTypeInfo> {
                            override fun onFailed(exception: AirwallexException) {
                                continuation.resume(Result.failure(exception))
                            }

                            override fun onSuccess(response: PaymentMethodTypeInfo) {
                                continuation.resume(Result.success(response))
                            }
                        }
                    )
                }

                else -> {
                    continuation.resume(
                        Result.failure(
                            AirwallexCheckoutException(message = "$paymentMethodTypeName just support one-off payment")
                        )
                    )
                }
            }
        }
    }

    fun trackPaymentCancelled() {
        AnalyticsLogger.logAction(
            actionName = EVENT_PAYMENT_CANCELLED,
            additionalInfo = additionalAnalyticsEventParams,
        )
    }

    fun trackPaymentLaunched() {
        AnalyticsLogger.logAction(
            actionName = EVENT_PAYMENT_LAUNCHED,
            additionalInfo = additionalAnalyticsEventParams,
        )
    }

    fun trackScreenViewed(eventName: String, params: Map<String, Any> = emptyMap()) {
        AnalyticsLogger.logPaymentView(
            viewName = eventName,
            additionalInfo = params + additionalAnalyticsEventParams,
        )
    }

    internal class Factory(
        private val application: Application,
        private val airwallex: Airwallex,
        private val session: AirwallexSession
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AirwallexCheckoutViewModel(
                application, airwallex, session
            ) as T
        }
    }
}
