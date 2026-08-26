package com.airwallex.android.core

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.airwallex.android.core.Airwallex.PaymentListener
import com.airwallex.android.core.Airwallex.PaymentResultListener
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.exception.AirwallexComponentDependencyException
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.exception.InvalidParamsException
import com.airwallex.android.core.extension.confirmGooglePayIntent
import com.airwallex.android.core.extension.convertToLegacySession
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.model.AirwallexPaymentRequestFlow
import com.airwallex.android.core.model.Billing
import com.airwallex.android.core.model.ConfirmPaymentIntentParams
import com.airwallex.android.core.model.CreatePaymentConsentParams
import com.airwallex.android.core.model.Dependency
import com.airwallex.android.core.model.Options
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.core.model.VerifyPaymentConsentParams
import com.airwallex.risk.AirwallexRisk
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.Locale
import kotlin.coroutines.cancellation.CancellationException

/**
 * Drives the legacy checkout flow for the deprecated session types ([AirwallexRecurringSession],
 * [AirwallexRecurringWithIntentSession]) and LPMs: a multi-step create-consent / verify / confirm
 * sequence, in contrast to the single confirm call of the unified flow. A [Session] is converted to
 * its legacy equivalent before running through the same path.
 */
internal class LegacyFlowCheckoutExecutor @Suppress("LongParameterList") constructor(
    private val fragment: Fragment?,
    private val activityProvider: () -> ComponentActivity,
    private val applicationContext: Context,
    private val paymentManager: PaymentManager,
    private val confirmPaymentService: ConfirmPaymentService,
    private val verificationService: PaymentConsentVerificationService,
    private val googlePayDelegate: GooglePayCheckoutDelegate,
    private val createCardPaymentMethod: (
        session: AirwallexSession,
        card: PaymentMethod.Card,
        billing: Billing?,
        saveCard: Boolean,
        listener: PaymentListener<PaymentMethod>
    ) -> Unit,
    private val buildCreatePaymentConsentOptions: (CreatePaymentConsentParams) -> Options.CreatePaymentConsentOptions,
) {

    /**
     * Routes old flow checkout based on session type and payment method.
     * Handles special cases for AirwallexRecurringSession with card payments.
     */
    @Suppress("LongParameterList")
    @UiThread
    fun checkout(
        session: AirwallexSession,
        paymentMethod: PaymentMethod,
        cvc: String? = null,
        additionalInfo: Map<String, String>? = null,
        flow: AirwallexPaymentRequestFlow? = null,
        listener: PaymentResultListener,
    ) {
        // SPECIAL HANDLING: AirwallexRecurringSession with card payments
        // Note: AirwallexRecurringSession always creates NEW payment consents, never uses existing ones
        if (session is AirwallexRecurringSession && paymentMethod.type == PaymentMethodType.CARD.value) {
            val card = paymentMethod.card
            if (card == null) {
                listener.onCompleted(
                    AirwallexPaymentStatus.Failure(
                        AirwallexCheckoutException(message = "Card is required for card payment")
                    )
                )
                return
            }
            createCardPaymentMethod(
                session,
                card,
                paymentMethod.billing,
                false,
                object : PaymentListener<PaymentMethod> {
                    override fun onSuccess(response: PaymentMethod) {
                        checkoutLegacySession(
                            session = session,
                            paymentMethod = response,
                            cvc = card.cvc,
                            listener = listener
                        )
                    }

                    override fun onFailed(exception: AirwallexException) {
                        listener.onCompleted(AirwallexPaymentStatus.Failure(exception))
                    }
                }
            )
            return
        }
        if (session is AirwallexRecurringSession && paymentMethod.type == PaymentMethodType.GOOGLEPAY.value) {
            checkoutGooglePay(session, listener)
            return
        }
        // OLD FLOW: Use legacy implementation for AirwallexRecurringSession and LPMs
        // Convert Session to legacy if needed
        if (session is Session) {
            handleNewSessionInOldFlow(session, paymentMethod, cvc, additionalInfo, flow, listener)
        } else {
            checkoutLegacySession(
                session = session,
                paymentMethod = paymentMethod,
                cvc = cvc,
                additionalInfo = additionalInfo,
                flow = flow,
                listener = listener
            )
        }
    }

    @Suppress("LongParameterList")
    private fun handleNewSessionInOldFlow(
        session: Session,
        paymentMethod: PaymentMethod,
        cvc: String?,
        additionalInfo: Map<String, String>?,
        flow: AirwallexPaymentRequestFlow?,
        listener: PaymentResultListener
    ) {
        activityProvider().lifecycleScope.launch {
            try {
                val legacySession = session.convertToLegacySession()
                checkoutLegacySession(
                    session = legacySession,
                    paymentMethod = paymentMethod,
                    cvc = cvc,
                    additionalInfo = additionalInfo,
                    flow = flow,
                    listener = listener
                )
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                listener.onCompleted(
                    AirwallexPaymentStatus.Failure(
                        AirwallexCheckoutException(
                            message = error.message,
                            e = error
                        )
                    )
                )
            }
        }
    }

    /**
     * Legacy checkout implementation for AirwallexRecurringSession and LPMs.
     * Handles all deprecated session types with the old flow.
     */
    @Suppress("LongParameterList")
    @UiThread
    private fun checkoutLegacySession(
        session: AirwallexSession,
        paymentMethod: PaymentMethod,
        paymentConsentId: String? = null,
        paymentConsent: PaymentConsent? = null,
        cvc: String? = null,
        additionalInfo: Map<String, String>? = null,
        flow: AirwallexPaymentRequestFlow? = null,
        listener: PaymentResultListener,
        saveCard: Boolean = false,
    ) {
        // Wrap listener at entry point to log payment result once
        val loggingListener = LoggingPaymentResultListener.wrap(listener, paymentMethod.type ?: "unknown")
        AirwallexLogger.info("Airwallex checkout: saveCard = $saveCard, paymentMethod.type = ${paymentMethod.type} session type = ${session.javaClass}")

        // Legacy flow implementation
        when (session) {
            is AirwallexPaymentSession -> {
                if (paymentMethod.type == PaymentMethodType.GOOGLEPAY.value) {
                    checkoutGooglePay(session, loggingListener)
                } else if (saveCard) {
                    createPaymentConsentAndConfirmIntent(
                        session,
                        paymentMethod,
                        cvc,
                        loggingListener
                    )
                } else {
                    session.resolvePaymentIntent(object :
                        PaymentIntentProvider.PaymentIntentCallback {
                        override fun onSuccess(paymentIntent: PaymentIntent) {
                            confirmPaymentIntent(
                                paymentIntentId = paymentIntent.id,
                                clientSecret = requireNotNull(paymentIntent.clientSecret),
                                paymentMethod = paymentMethod,
                                cvc = cvc,
                                currency = session.currency,
                                customerId = session.customerId,
                                paymentConsentId = paymentConsent?.id ?: paymentConsentId,
                                additionalInfo = additionalInfo,
                                returnUrl = if (paymentMethod.type == PaymentMethodType.CARD.value) {
                                    AirwallexPlugins.environment.threeDsReturnUrl()
                                } else session.returnUrl,
                                autoCapture = session.autoCapture,
                                flow = flow,
                                listener = loggingListener
                            )
                        }

                        override fun onError(error: Throwable) {
                            loggingListener.onCompleted(
                                AirwallexPaymentStatus.Failure(
                                    AirwallexCheckoutException(message = error.message, e = error)
                                )
                            )
                        }
                    })
                }
            }

            is AirwallexRecurringSession, is AirwallexRecurringWithIntentSession ->
                createPaymentConsentAndConfirmIntent(session, paymentMethod, cvc, loggingListener)

            else -> {
                loggingListener.onCompleted(
                    AirwallexPaymentStatus.Failure(
                        AirwallexCheckoutException(message = "Unknown legacy session type: ${session.javaClass}")
                    )
                )
            }
        }
    }

    @UiThread
    private fun createPaymentConsentAndConfirmIntent(
        session: AirwallexSession,
        paymentMethod: PaymentMethod,
        cvc: String? = null,
        listener: PaymentResultListener
    ) {
        fun confirmPaymentIntent(
            session: AirwallexPaymentSession,
            consent: PaymentConsent? = null
        ) {
            session.resolvePaymentIntent(object : PaymentIntentProvider.PaymentIntentCallback {
                override fun onSuccess(paymentIntent: PaymentIntent) {
                    confirmPaymentIntent(
                        paymentIntentId = paymentIntent.id,
                        clientSecret = requireNotNull(paymentIntent.clientSecret),
                        paymentMethod = paymentMethod,
                        cvc = cvc,
                        customerId = session.customerId,
                        paymentConsentId = consent?.id,
                        returnUrl = if (paymentMethod.type
                            == PaymentMethodType.CARD.value
                        ) {
                            AirwallexPlugins.environment.threeDsReturnUrl()
                        } else session.returnUrl,
                        autoCapture = session.autoCapture,
                        locale = session.locale,
                        listener = listener
                    )
                }

                override fun onError(error: Throwable) {
                    listener.onCompleted(
                        AirwallexPaymentStatus.Failure(
                            AirwallexCheckoutException(
                                message = error.message,
                                e = error
                            )
                        )
                    )
                }
            })
        }

        when (session) {
            is AirwallexPaymentSession -> {
                session.resolvePaymentIntent(object : PaymentIntentProvider.PaymentIntentCallback {
                    override fun onSuccess(paymentIntent: PaymentIntent) {
                        createPaymentConsent(
                            clientSecret = requireNotNull(paymentIntent.clientSecret),
                            customerId = requireNotNull(session.customerId),
                            paymentMethod = paymentMethod,
                            nextTriggeredBy = PaymentConsent.NextTriggeredBy.CUSTOMER,
                            merchantTriggerReason = null,
                            listener = object : PaymentListener<PaymentConsent> {
                                override fun onFailed(exception: AirwallexException) {
                                    confirmPaymentIntent(session)
                                }

                                override fun onSuccess(response: PaymentConsent) {
                                    confirmPaymentIntent(session, response)
                                }
                            }
                        )
                    }

                    override fun onError(error: Throwable) {
                        listener.onCompleted(
                            AirwallexPaymentStatus.Failure(
                                AirwallexCheckoutException(message = error.message, e = error)
                            )
                        )
                    }
                })
            }

            is AirwallexRecurringSession -> {
                val customerId = session.customerId
                val clientSecret = session.clientSecret
                createPaymentConsent(
                    clientSecret = clientSecret,
                    customerId = customerId,
                    paymentMethod = paymentMethod,
                    nextTriggeredBy = session.nextTriggerBy,
                    merchantTriggerReason = session.merchantTriggerReason,
                    listener = object : PaymentListener<PaymentConsent> {
                        override fun onFailed(exception: AirwallexException) {
                            listener.onCompleted(
                                AirwallexPaymentStatus.Failure(exception)
                            )
                        }

                        override fun onSuccess(response: PaymentConsent) {
                            verifyPaymentConsent(
                                paymentConsent = response,
                                currency = session.currency,
                                amount = session.amount,
                                cvc = cvc,
                                returnUrl = if (paymentMethod.type == PaymentMethodType.CARD.value) AirwallexPlugins.environment.threeDsReturnUrl() else session.returnUrl,
                                locale = session.locale,
                                listener = listener
                            )
                        }
                    }
                )
            }

            is AirwallexRecurringWithIntentSession -> {
                session.resolvePaymentIntent(object : PaymentIntentProvider.PaymentIntentCallback {
                    override fun onSuccess(paymentIntent: PaymentIntent) {
                        createPaymentConsent(
                            clientSecret = requireNotNull(paymentIntent.clientSecret),
                            customerId = session.customerId,
                            paymentMethod = paymentMethod,
                            nextTriggeredBy = session.nextTriggerBy,
                            merchantTriggerReason = session.merchantTriggerReason,
                            listener = object : PaymentListener<PaymentConsent> {
                                override fun onFailed(exception: AirwallexException) {
                                    listener.onCompleted(AirwallexPaymentStatus.Failure(exception))
                                }

                                override fun onSuccess(response: PaymentConsent) {
                                    when (paymentMethod.type) {
                                        PaymentMethodType.CARD.value -> {
                                            confirmPaymentIntent(
                                                paymentIntentId = paymentIntent.id,
                                                clientSecret = requireNotNull(paymentIntent.clientSecret),
                                                paymentMethod = paymentMethod,
                                                cvc = cvc,
                                                customerId = session.customerId,
                                                paymentConsentId = response.id,
                                                returnUrl = AirwallexPlugins.environment.threeDsReturnUrl(),
                                                autoCapture = session.autoCapture,
                                                locale = session.locale,
                                                listener = listener
                                            )
                                        }

                                        else -> {
                                            // this should not happen
                                            verifyPaymentConsent(
                                                paymentConsent = response,
                                                currency = session.currency,
                                                amount = session.amount,
                                                returnUrl = session.returnUrl,
                                                locale = session.locale,
                                                listener = listener
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }

                    override fun onError(error: Throwable) {
                        listener.onCompleted(
                            AirwallexPaymentStatus.Failure(
                                AirwallexCheckoutException(message = error.message, e = error)
                            )
                        )
                    }
                })
            }
        }
    }

    private fun createGooglePayConsentAndVerify(
        session: AirwallexRecurringSession,
        listener: PaymentResultListener,
        googlePay: PaymentMethod.GooglePay
    ) {
        val paymentMethod = PaymentMethod.Builder()
            .setType(PaymentMethodType.GOOGLEPAY.value)
            .setGooglePay(googlePay)
            .build()
        createPaymentConsent(
            clientSecret = session.clientSecret,
            customerId = session.customerId,
            paymentMethod = paymentMethod,
            nextTriggeredBy = session.nextTriggerBy,
            merchantTriggerReason = session.merchantTriggerReason,
            listener = object : PaymentListener<PaymentConsent> {
                override fun onFailed(exception: AirwallexException) {
                    listener.onCompleted(
                        AirwallexPaymentStatus.Failure(exception)
                    )
                }

                override fun onSuccess(response: PaymentConsent) {
                    verifyPaymentConsent(
                        paymentConsent = response,
                        currency = session.currency,
                        amount = session.amount,
                        cvc = null,
                        returnUrl = AirwallexPlugins.environment.threeDsReturnUrl(),
                        locale = session.locale,
                        listener = listener
                    )
                }
            }
        )
    }

    private fun createGooglePayConsentAndConfirm(
        session: AirwallexRecurringWithIntentSession,
        listener: PaymentResultListener,
        googlePay: PaymentMethod.GooglePay
    ) {
        val paymentMethod = PaymentMethod.Builder()
            .setType(PaymentMethodType.GOOGLEPAY.value)
            .setGooglePay(googlePay)
            .build()
        session.resolvePaymentIntent(object : PaymentIntentProvider.PaymentIntentCallback {
            override fun onSuccess(paymentIntent: PaymentIntent) {
                createPaymentConsent(
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    customerId = session.customerId,
                    paymentMethod = paymentMethod,
                    nextTriggeredBy = session.nextTriggerBy,
                    merchantTriggerReason = session.merchantTriggerReason,
                    listener = object : PaymentListener<PaymentConsent> {
                        override fun onFailed(exception: AirwallexException) {
                            listener.onCompleted(AirwallexPaymentStatus.Failure(exception))
                        }

                        override fun onSuccess(response: PaymentConsent) {
                            confirmPaymentIntent(
                                paymentIntentId = paymentIntent.id,
                                clientSecret = requireNotNull(paymentIntent.clientSecret),
                                paymentMethod = paymentMethod,
                                cvc = null,
                                customerId = session.customerId,
                                paymentConsentId = response.id,
                                returnUrl = AirwallexPlugins.environment.threeDsReturnUrl(),
                                autoCapture = session.autoCapture,
                                listener = listener
                            )
                        }
                    }
                )
            }

            override fun onError(error: Throwable) {
                listener.onCompleted(
                    AirwallexPaymentStatus.Failure(
                        AirwallexCheckoutException(
                            message = error.message,
                            e = error
                        )
                    )
                )
            }
        })
    }

    private fun checkoutGooglePay(
        session: AirwallexSession,
        listener: PaymentResultListener,
    ) {
        googlePayDelegate.acquireToken(session, listener) { googlePay, provider ->
            when (session) {
                is AirwallexPaymentSession -> {
                    session.resolvePaymentIntent(object : PaymentIntentProvider.PaymentIntentCallback {
                        override fun onSuccess(paymentIntent: PaymentIntent) {
                            provider.get().confirmGooglePayIntent(
                                fragment = fragment,
                                activityProvider = activityProvider,
                                paymentManager = paymentManager,
                                applicationContext = applicationContext,
                                paymentIntentId = paymentIntent.id,
                                clientSecret = requireNotNull(paymentIntent.clientSecret),
                                googlePay = googlePay,
                                autoCapture = session.autoCapture,
                                listener = listener
                            )
                        }

                        override fun onError(error: Throwable) {
                            listener.onCompleted(AirwallexPaymentStatus.Failure(AirwallexCheckoutException(message = error.message, e = error)))
                        }
                    })
                }

                is AirwallexRecurringSession -> createGooglePayConsentAndVerify(session, listener, googlePay)

                is AirwallexRecurringWithIntentSession -> createGooglePayConsentAndConfirm(session, listener, googlePay)

                else -> Unit
            }
        }
    }

    @Suppress("LongParameterList")
    private fun confirmPaymentIntent(
        paymentIntentId: String,
        clientSecret: String,
        paymentMethod: PaymentMethod,
        cvc: String? = null,
        currency: String? = null,
        customerId: String? = null,
        paymentConsentId: String? = null,
        additionalInfo: Map<String, String>? = null,
        returnUrl: String? = null,
        autoCapture: Boolean = true,
        flow: AirwallexPaymentRequestFlow? = null,
        locale: Locale? = null,
        listener: PaymentResultListener
    ) {
        val params = when (val paymentMethodType = requireNotNull(paymentMethod.type)) {
            PaymentMethodType.CARD.value -> {
                ConfirmPaymentIntentParams.createCardParams(
                    paymentIntentId = paymentIntentId,
                    clientSecret = clientSecret,
                    paymentMethod = paymentMethod,
                    cvc = cvc,
                    customerId = customerId,
                    paymentConsentId = paymentConsentId,
                    returnUrl = returnUrl,
                    autoCapture = autoCapture
                )
            }

            PaymentMethodType.GOOGLEPAY.value -> {
                ConfirmPaymentIntentParams.createGooglePayParams(
                    paymentIntentId = paymentIntentId,
                    clientSecret = clientSecret,
                    paymentMethod = paymentMethod,
                    cvc = cvc,
                    customerId = customerId,
                    paymentConsentId = paymentConsentId,
                    returnUrl = returnUrl,
                    autoCapture = autoCapture
                )
            }

            else -> {
                ConfirmPaymentIntentParams.createThirdPartPayParams(
                    paymentMethodType = paymentMethodType,
                    paymentIntentId = paymentIntentId,
                    clientSecret = clientSecret,
                    customerId = customerId,
                    paymentConsentId = paymentConsentId,
                    currency = currency,
                    additionalInfo = additionalInfo,
                    returnUrl = returnUrl,
                    flow = flow
                )
            }
        }
        confirmPaymentService.confirm(params, locale, listener)
    }

    @Suppress("LongParameterList")
    private fun createPaymentConsent(
        clientSecret: String,
        customerId: String,
        paymentMethod: PaymentMethod,
        nextTriggeredBy: PaymentConsent.NextTriggeredBy = PaymentConsent.NextTriggeredBy.MERCHANT,
        merchantTriggerReason: PaymentConsent.MerchantTriggerReason? = PaymentConsent.MerchantTriggerReason.UNSCHEDULED,
        listener: PaymentListener<PaymentConsent>
    ) {
        val params: CreatePaymentConsentParams =
            when (val paymentMethodType = requireNotNull(paymentMethod.type)) {
                PaymentMethodType.CARD.value -> {
                    CreatePaymentConsentParams.createCardParams(
                        clientSecret = clientSecret,
                        customerId = customerId,
                        paymentMethodId = requireNotNull(paymentMethod.id),
                        nextTriggeredBy = nextTriggeredBy,
                        merchantTriggerReason = merchantTriggerReason,
                    )
                }

                PaymentMethodType.GOOGLEPAY.value -> {
                    CreatePaymentConsentParams.createGooglePayParams(
                        clientSecret = clientSecret,
                        customerId = customerId,
                        googlePay = requireNotNull(paymentMethod.googlePay),
                        nextTriggeredBy = nextTriggeredBy,
                        merchantTriggerReason = merchantTriggerReason,
                    )
                }

                else -> {
                    CreatePaymentConsentParams.createThirdPartParams(
                        paymentMethodType = paymentMethodType,
                        clientSecret = clientSecret,
                        customerId = customerId
                    )
                }
            }
        paymentManager.startOperation(
            buildCreatePaymentConsentOptions(params),
            listener
        )
    }

    @Suppress("LongParameterList")
    private fun verifyPaymentConsent(
        paymentConsent: PaymentConsent,
        currency: String,
        amount: BigDecimal? = null,
        cvc: String? = null,
        returnUrl: String? = null,
        locale: Locale? = null,
        listener: PaymentResultListener
    ) {
        if (paymentConsent.requiresCvc && cvc == null) {
            listener.onCompleted(
                AirwallexPaymentStatus.Failure(InvalidParamsException(message = "CVC is required!"))
            )
            return
        }
        val paymentMethodType = paymentConsent.paymentMethod?.type
        try {
            val provider =
                AirwallexPlugins.getProvider(ActionComponentProviderType.fromValue(paymentMethodType))
            if (provider == null) {
                listener.onCompleted(
                    AirwallexPaymentStatus.Failure(
                        AirwallexComponentDependencyException(
                            dependency = Dependency.fromValue(
                                PaymentMethodType.fromValue(paymentMethodType).dependencyName
                            )
                        )
                    )
                )
                return
            }
            val params = VerifyPaymentConsentParams.createParamsByMethodType(
                paymentMethodType = requireNotNull(paymentMethodType),
                clientSecret = requireNotNull(paymentConsent.clientSecret),
                paymentConsentId = requireNotNull(paymentConsent.id),
                amount = amount,
                currency = currency,
                cvc = cvc,
                returnUrl = returnUrl
            )
            val device = paymentManager.buildDeviceInfo(AirwallexRisk.sessionId.toString())
            verificationService.verify(device, params, locale, listener)
        } catch (e: Exception) {
            listener.onCompleted(
                AirwallexPaymentStatus.Failure(AirwallexCheckoutException(e = e))
            )
        }
    }
}
