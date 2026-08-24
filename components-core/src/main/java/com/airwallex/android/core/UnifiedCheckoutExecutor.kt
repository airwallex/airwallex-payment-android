package com.airwallex.android.core

import androidx.annotation.UiThread
import com.airwallex.android.core.Airwallex.PaymentResultListener
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.model.ConfirmPaymentIntentParams
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentConsentOptions
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodType

/**
 * Drives the unified checkout for the new [Session] flow: a single confirm call replaces the
 * multi-step consent/confirm sequence of the legacy flow. Google Pay first acquires a token via
 * [googlePayDelegate], then continues through the same confirm path as card.
 */
internal class UnifiedCheckoutExecutor(
    private val confirmPaymentService: ConfirmPaymentService,
    private val googlePayDelegate: GooglePayCheckoutDelegate,
) {

    @Suppress("LongParameterList")
    @UiThread
    fun checkout(
        session: Session,
        paymentMethod: PaymentMethod,
        cvc: String? = null,
        saveCard: Boolean = false,
        paymentConsent: PaymentConsent? = null,
        listener: PaymentResultListener
    ) {
        AirwallexLogger.info("Airwallex unified checkout: saveCard = $saveCard, paymentMethod.type = ${paymentMethod.type} session type = ${session.javaClass}")

        if (paymentMethod.type == PaymentMethodType.GOOGLEPAY.value) {
            googlePayDelegate.acquireToken(session, listener) { googlePay, _ ->
                proceedWithUnifiedPayment(
                    session = session,
                    paymentMethod = paymentMethod.copy(googlePay = googlePay),
                    cvc = null,
                    saveCard = saveCard,
                    paymentConsent = paymentConsent,
                    listener = listener
                )
            }
            return
        }

        proceedWithUnifiedPayment(session, paymentMethod, cvc, saveCard, paymentConsent, listener)
    }

    @Suppress("LongParameterList")
    private fun proceedWithUnifiedPayment(
        session: Session,
        paymentMethod: PaymentMethod,
        cvc: String?,
        saveCard: Boolean,
        paymentConsent: PaymentConsent?,
        listener: PaymentResultListener
    ) {
        session.resolvePaymentIntent(object : PaymentIntentProvider.PaymentIntentCallback {
            override fun onSuccess(paymentIntent: PaymentIntent) {
                confirmPaymentIntentUnified(
                    session = session,
                    paymentIntentId = paymentIntent.id,
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    paymentMethod = paymentMethod,
                    cvc = cvc,
                    saveCard = saveCard,
                    paymentConsent = paymentConsent,
                    returnUrl = if (paymentMethod.type == PaymentMethodType.CARD.value || paymentMethod.type == PaymentMethodType.GOOGLEPAY.value) {
                        AirwallexPlugins.environment.threeDsReturnUrl()
                    } else session.returnUrl,
                    autoCapture = session.autoCapture,
                    listener = listener
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

    @Suppress("LongParameterList")
    private fun confirmPaymentIntentUnified(
        session: Session,
        paymentIntentId: String,
        clientSecret: String,
        paymentMethod: PaymentMethod,
        cvc: String? = null,
        saveCard: Boolean = false,
        paymentConsent: PaymentConsent? = null,
        returnUrl: String? = null,
        autoCapture: Boolean = true,
        listener: PaymentResultListener
    ) {
        val paymentConsentOptions = when {
            // 1. Session already has consent options
            session.paymentConsentOptions != null -> session.paymentConsentOptions

            // MIT consent used for one-off payment → override to create CIT consent
            paymentConsent != null &&
                    paymentConsent.nextTriggeredBy == PaymentConsent.NextTriggeredBy.MERCHANT &&
                    session.isOneOffPayment -> PaymentConsentOptions(
                nextTriggeredBy = PaymentConsent.NextTriggeredBy.CUSTOMER
            )

            // CIT with new card and saveCard is enabled → create CIT consent
            saveCard && session.customerId != null -> PaymentConsentOptions(
                nextTriggeredBy = PaymentConsent.NextTriggeredBy.CUSTOMER
            )
            // one off transaction either with CIT or with new card without saving
            else -> null
        }

        val params = when (paymentMethod.type) {
            PaymentMethodType.GOOGLEPAY.value -> {
                ConfirmPaymentIntentParams.createGooglePayParams(
                    paymentIntentId = paymentIntentId,
                    clientSecret = clientSecret,
                    paymentMethod = paymentMethod,
                    cvc = cvc,
                    customerId = session.customerId,
                    paymentConsentId = paymentConsent?.id,
                    paymentConsentOptions = paymentConsentOptions,
                    returnUrl = returnUrl,
                    autoCapture = autoCapture
                )
            }

            else -> {
                ConfirmPaymentIntentParams.createCardParams(
                    paymentIntentId = paymentIntentId,
                    clientSecret = clientSecret,
                    paymentMethod = paymentMethod,
                    cvc = cvc,
                    customerId = session.customerId,
                    paymentConsentId = paymentConsent?.id,
                    paymentConsentOptions = paymentConsentOptions,
                    returnUrl = returnUrl,
                    autoCapture = autoCapture
                )
            }
        }

        confirmPaymentService.confirm(params, session.locale, listener)
    }
}
