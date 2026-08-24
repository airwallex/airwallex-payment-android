package com.airwallex.android.core

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import com.airwallex.android.core.Airwallex.PaymentListener
import com.airwallex.android.core.Airwallex.PaymentResultListener
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.exception.AirwallexComponentDependencyException
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.model.ConfirmPaymentIntentParams
import com.airwallex.android.core.model.Dependency
import com.airwallex.android.core.model.Device
import com.airwallex.android.core.model.Options
import com.airwallex.android.core.model.PaymentConsentReference
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PaymentIntentConfirmRequest
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodOptions
import com.airwallex.android.core.model.PaymentMethodRequest
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.core.model.ThreeDSecure
import com.airwallex.risk.AirwallexRisk
import java.util.Locale
import java.util.UUID

/**
 * Confirms a payment intent against the Airwallex backend and drives any resulting next action
 * (e.g. 3DS). This is the shared terminal step for every checkout flow on the [AirwallexSession]
 * family, and remains the low-level entry point for the deprecated device-fingerprinting API.
 */
internal interface ConfirmPaymentService {

    fun confirm(
        params: ConfirmPaymentIntentParams,
        locale: Locale?,
        listener: PaymentResultListener
    )

    fun confirmWithDevice(
        device: Device?,
        params: ConfirmPaymentIntentParams,
        locale: Locale?,
        listener: PaymentResultListener
    )
}

internal class DefaultConfirmPaymentService(
    private val paymentManager: PaymentManager,
    private val fragment: Fragment?,
    private val activityProvider: () -> ComponentActivity,
    private val applicationContext: Context,
) : ConfirmPaymentService {

    override fun confirm(
        params: ConfirmPaymentIntentParams,
        locale: Locale?,
        listener: PaymentResultListener
    ) {
        val paymentMethodType = params.paymentMethodType
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
            val device = paymentManager.buildDeviceInfo(AirwallexRisk.sessionId.toString())
            confirmWithDevice(
                device = device,
                params = params,
                locale = locale,
                listener = listener
            )
        } catch (e: Exception) {
            listener.onCompleted(
                AirwallexPaymentStatus.Failure(AirwallexCheckoutException(e = e))
            )
        }
    }

    override fun confirmWithDevice(
        device: Device?,
        params: ConfirmPaymentIntentParams,
        locale: Locale?,
        listener: PaymentResultListener
    ) {
        val loggingListener = LoggingPaymentResultListener.wrap(listener, params.paymentMethodType)
        val options = when (params.paymentMethodType) {
            // The backend requires passing parameters using the card field in payment_method_options, even if the payment type is Google Pay.
            PaymentMethodType.CARD.value, PaymentMethodType.GOOGLEPAY.value -> {
                buildCardPaymentIntentOptions(params, device)
            }

            else -> {
                buildThirdPartPaymentIntentOptions(params, device)
            }
        }
        paymentManager.startOperation(
            options,
            object : PaymentListener<PaymentIntent> {
                override fun onFailed(exception: AirwallexException) {
                    AirwallexLogger.error("Airwallex confirmPaymentIntentWithDevice fail: type = ${params.paymentMethodType}, onFailed = ${exception.message}")
                    loggingListener.onCompleted(AirwallexPaymentStatus.Failure(exception))
                }

                override fun onSuccess(response: PaymentIntent) {
                    AirwallexLogger.info("Airwallex confirmPaymentIntentWithDevice success: type = ${params.paymentMethodType}, nextAction = ${response.nextAction?.type}")
                    // If the nextAction is null, the payment is completed
                    if (response.nextAction == null) {
                        loggingListener.onCompleted(
                            AirwallexPaymentStatus.Success(
                                response.id,
                                params.paymentConsentId
                            )
                        )
                        return
                    }
                    val provider =
                        AirwallexPlugins.getProvider(ActionComponentProviderType.fromValue(params.paymentMethodType))
                    if (provider == null) {
                        AirwallexLogger.error("Airwallex confirmPaymentIntentWithDevice: type = ${params.paymentMethodType}, Provider is null")
                        loggingListener.onCompleted(
                            AirwallexPaymentStatus.Failure(AirwallexCheckoutException(message = "Missing dependency!"))
                        )
                        return
                    }
                    val nextActionModel = when (params.paymentMethodType) {
                        PaymentMethodType.CARD.value, PaymentMethodType.GOOGLEPAY.value -> CardNextActionModel(
                            paymentManager = paymentManager,
                            clientSecret = params.clientSecret,
                            device = device,
                            paymentIntentId = response.id,
                            currency = response.currency,
                            amount = response.amount,
                            activityProvider = activityProvider,
                            locale = locale
                        )

                        else -> null
                    }
                    provider.get().handlePaymentIntentResponse(
                        response.id,
                        response.nextAction,
                        fragment,
                        activityProvider(),
                        applicationContext,
                        nextActionModel,
                        loggingListener,
                        params.paymentConsentId
                    )
                }
            }
        )
    }

    private fun buildCardPaymentIntentOptions(
        params: ConfirmPaymentIntentParams,
        device: Device?
    ): Options.ConfirmPaymentIntentOptions {
        val threeDSecure = ThreeDSecure.Builder()
            .setReturnUrl(AirwallexPlugins.environment.threeDsReturnUrl())
            .build()

        // Determine which payment approach to use based on payment method ID presence
        // If paymentMethod.id exists → it's from a saved card (consent)
        // If paymentMethod.id is null → it's a new card
        val hasPaymentMethodId = params.paymentMethod?.id != null

        // Build payment_consent_reference (OLD flow: CIT consent without consent options)
        val paymentConsentReference =
            if (params.paymentConsentId != null && params.paymentConsentOptions == null) {
                PaymentConsentReference.Builder()
                    .setId(params.paymentConsentId)
                    .setCvc(params.cvc)
                    .build()
            } else null

        // Build payment_method based on scenario
        val paymentMethodRequest =
            buildPaymentMethodRequest(paymentConsentReference, hasPaymentMethodId, params)

        val builder = PaymentIntentConfirmRequest.Builder(
            requestId = UUID.randomUUID().toString()
        )
            .setPaymentMethodOptions(
                PaymentMethodOptions.Builder()
                    .setCardOptions(
                        PaymentMethodOptions.CardOptions.Builder()
                            .setAutoCapture(params.autoCapture)
                            .setThreeDSecure(threeDSecure)
                            .build()
                    )
                    .build()
            )
            .setCustomerId(params.customerId)
            .setDevice(device)
            .setPaymentConsentReference(paymentConsentReference) // Old flow
            .setPaymentConsent(params.paymentConsentOptions) // New flow
            .setPaymentMethodRequest(paymentMethodRequest)

        if (params.returnUrl != null) {
            builder.setReturnUrl(params.returnUrl)
        }

        return Options.ConfirmPaymentIntentOptions(
            clientSecret = params.clientSecret,
            paymentIntentId = params.paymentIntentId,
            request = builder.build()
        )
    }

    private fun buildPaymentMethodRequest(
        paymentConsentReference: PaymentConsentReference?,
        hasPaymentMethodId: Boolean,
        params: ConfirmPaymentIntentParams
    ): PaymentMethodRequest? {
        val paymentMethodRequest = when {
            // Scenario 1: Google Pay payment
            // → payment_method: { type: "googlepay", googlepay: {...} }
            params.paymentMethod?.type == PaymentMethodType.GOOGLEPAY.value -> {
                PaymentMethodRequest.Builder(PaymentMethodType.GOOGLEPAY.value)
                    .setGooglePayPaymentMethodRequest(params.paymentMethod.googlePay)
                    .build()
            }

            // Scenario 2: OLD flow - CIT consent without consent options
            // → No payment_method in request body (uses payment_consent_reference)
            paymentConsentReference != null -> null

            // Scenario 3: NEW flow - Saved card with payment method ID AND consent options
            // → payment_method: { type, id, card?: { cvc } }
            hasPaymentMethodId && params.paymentConsentOptions != null -> {
                val builder = PaymentMethodRequest.Builder(params.paymentMethodType)
                    .setId(params.paymentMethod?.id) // Payment method ID from consent

                // Include CVC if provided
                if (params.cvc != null) {
                    builder.setCardPaymentMethodRequest(
                        card = PaymentMethod.Card.Builder()
                            .setCvc(params.cvc)
                            .build(),
                        billing = null
                    )
                }
                builder.build()
            }

            // Scenario 4: New card payment (with or without saveCard)
            // → payment_method: { type, card: {...}, billing: {...} }
            else -> {
                PaymentMethodRequest.Builder(params.paymentMethodType)
                    .setCardPaymentMethodRequest(
                        card = params.paymentMethod?.card,
                        billing = params.paymentMethod?.billing
                    )
                    .build()
            }
        }
        return paymentMethodRequest
    }

    private fun buildThirdPartPaymentIntentOptions(
        params: ConfirmPaymentIntentParams,
        device: Device?
    ): Options.ConfirmPaymentIntentOptions {

        val paymentConsentReference: PaymentConsentReference?
        val paymentMethodRequest: PaymentMethodRequest?

        if (params.paymentConsentId != null) {
            paymentConsentReference = PaymentConsentReference.Builder()
                .setId(params.paymentConsentId)
                .build()
            paymentMethodRequest = null
        } else {
            paymentConsentReference = null
            val builder = PaymentMethodRequest.Builder(params.paymentMethodType)
            val additionalInfo = params.additionalInfo
            if (additionalInfo != null) {
                builder.setThirdPartyPaymentMethodRequest(
                    additionalInfo = additionalInfo,
                    flow = params.flow
                )
            } else {
                builder.setThirdPartyPaymentMethodRequest(
                    flow = params.flow
                )
            }
            paymentMethodRequest = builder.build()
        }
        val request = PaymentIntentConfirmRequest.Builder(
            requestId = UUID.randomUUID().toString()
        )
            .setPaymentMethodRequest(paymentMethodRequest)
            .setCustomerId(params.customerId)
            .setDevice(device)
            .setPaymentConsentReference(paymentConsentReference)
            .build()

        return Options.ConfirmPaymentIntentOptions(
            clientSecret = params.clientSecret,
            paymentIntentId = params.paymentIntentId,
            request = request
        )
    }
}
