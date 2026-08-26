package com.airwallex.android.core

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import com.airwallex.android.core.Airwallex.PaymentListener
import com.airwallex.android.core.Airwallex.PaymentResultListener
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.log.AnalyticsLogger.Field
import com.airwallex.android.core.model.Device
import com.airwallex.android.core.model.Options
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentConsentVerifyRequest
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.core.model.VerifyPaymentConsentParams
import java.util.Locale
import java.util.UUID

/**
 * Verifies a [PaymentConsent] against the Airwallex backend and drives any resulting next action
 * (e.g. 3DS). Shared by the legacy recurring-consent checkout paths and the low-level public
 * device-fingerprinting API [Airwallex.verifyPaymentConsent].
 */
internal interface PaymentConsentVerificationService {

    fun verify(
        device: Device,
        params: VerifyPaymentConsentParams,
        locale: Locale?,
        listener: PaymentResultListener
    )
}

internal class DefaultPaymentConsentVerificationService(
    private val paymentManager: PaymentManager,
    private val fragment: Fragment?,
    private val activityProvider: () -> ComponentActivity,
    private val applicationContext: Context,
) : PaymentConsentVerificationService {

    override fun verify(
        device: Device,
        params: VerifyPaymentConsentParams,
        locale: Locale?,
        listener: PaymentResultListener
    ) {
        AirwallexLogger.info("Airwallex verifyPaymentConsent: type = ${params.paymentMethodType}")
        val paymentMethodType = params.paymentMethodType
        // Wrap listener at entry point to log payment result once
        val loggingListener = LoggingPaymentResultListener.wrap(listener, paymentMethodType)
        val verificationOptions = when (paymentMethodType) {
            // The backend requires passing parameters using the card field in verification_options, even if the payment type is Google Pay.
            PaymentMethodType.CARD.value, PaymentMethodType.GOOGLEPAY.value -> PaymentConsentVerifyRequest.VerificationOptions(
                type = PaymentMethodType.CARD.value,
                cardOptions = PaymentConsentVerifyRequest.CardVerificationOptions(
                    amount = params.amount,
                    currency = params.currency,
                    cvc = params.cvc,
                )
            )

            else -> {
                PaymentConsentVerifyRequest.VerificationOptions(
                    type = paymentMethodType,
                    thirdPartOptions = PaymentConsentVerifyRequest.ThirdPartVerificationOptions()
                )
            }
        }

        paymentManager.startOperation(
            Options.VerifyPaymentConsentOptions(
                clientSecret = params.clientSecret,
                paymentConsentId = params.paymentConsentId,
                request = PaymentConsentVerifyRequest.Builder()
                    .setRequestId(UUID.randomUUID().toString())
                    .setVerificationOptions(verificationOptions)
                    .setDevice(device)
                    .setReturnUrl(params.returnUrl)
                    .build()
            ),
            object : PaymentListener<PaymentConsent> {
                override fun onFailed(exception: AirwallexException) {
                    loggingListener.onCompleted(AirwallexPaymentStatus.Failure(exception))
                }

                override fun onSuccess(response: PaymentConsent) {
                    handlePaymentConsentVerifySuccess(
                        response = response,
                        params = params,
                        paymentMethodType = paymentMethodType,
                        locale = locale,
                        loggingListener = loggingListener
                    )
                }
            }
        )
    }

    @Suppress("LongMethod")
    private fun handlePaymentConsentVerifySuccess(
        response: PaymentConsent,
        params: VerifyPaymentConsentParams,
        paymentMethodType: String,
        locale: Locale?,
        loggingListener: PaymentResultListener
    ) {
        // for redirect, initialPaymentIntentId is empty now. so we don support recurring in redirect flow
        val paymentIntentId = response.initialPaymentIntentId

        if (response.nextAction == null) {
            loggingListener.onCompleted(
                AirwallexPaymentStatus.Success(paymentIntentId, response.id)
            )
            return
        }

        val provider = AirwallexPlugins.getProvider(
            ActionComponentProviderType.fromValue(paymentMethodType)
        )
        if (provider == null) {
            loggingListener.onCompleted(
                AirwallexPaymentStatus.Failure(
                    AirwallexCheckoutException(message = "Missing dependency!")
                )
            )
            return
        }

        when (paymentMethodType) {
            PaymentMethodType.CARD.value, PaymentMethodType.GOOGLEPAY.value -> {
                if (paymentIntentId.isNullOrEmpty()) {
                    AnalyticsLogger.logError(
                        "initialPaymentIntentId_null_or_empty",
                        mapOf(Field.TYPE to paymentMethodType)
                    )
                    AirwallexLogger.error("Airwallex verifyPaymentConsent: type = $paymentMethodType, paymentIntentId isNullOrEmpty")
                    loggingListener.onCompleted(
                        AirwallexPaymentStatus.Failure(
                            AirwallexCheckoutException(message = "Unsupported payment method")
                        )
                    )
                    return
                }

                val nextActionModel = createCardNextActionModel(
                    params = params,
                    paymentIntentId = paymentIntentId,
                    locale = locale
                )

                provider.get().handlePaymentIntentResponse(
                    paymentIntentId,
                    response.nextAction,
                    fragment,
                    activityProvider(),
                    applicationContext,
                    nextActionModel,
                    loggingListener,
                    response.id
                )
            }

            else ->
                provider.get().handlePaymentIntentResponse(
                    null,
                    response.nextAction,
                    fragment,
                    activityProvider(),
                    applicationContext,
                    null,
                    loggingListener,
                    response.id
                )
        }
    }

    private fun createCardNextActionModel(
        params: VerifyPaymentConsentParams,
        paymentIntentId: String,
        locale: Locale?
    ): CardNextActionModel {
        return CardNextActionModel(
            paymentManager = paymentManager,
            clientSecret = params.clientSecret,
            device = null,
            paymentIntentId = paymentIntentId,
            currency = requireNotNull(params.currency),
            amount = requireNotNull(params.amount),
            activityProvider = activityProvider,
            locale = locale
        )
    }
}
