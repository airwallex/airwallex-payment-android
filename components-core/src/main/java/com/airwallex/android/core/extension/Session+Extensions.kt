package com.airwallex.android.core.extension

import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.AirwallexRecurringSession
import com.airwallex.android.core.AirwallexRecurringWithIntentSession
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.PaymentIntentProvider
import com.airwallex.android.core.Session
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.resolvePaymentIntent

/**
 * Converts the current [Session] instance to a legacy [AirwallexSession] object.
 *
 * This conversion is primarily required for Local Payment Methods (LPM),
 * as they are not yet supported by the unified Session flow.
 *
 * Determines which legacy session to create based on Session properties:
 * - `paymentConsentOptions == null` → [AirwallexPaymentSession] (one-off)
 * - `paymentConsentOptions != null && paymentIntent.amount == 0` → [AirwallexRecurringSession]
 * - `paymentConsentOptions != null && paymentIntent.amount > 0` → [AirwallexRecurringWithIntentSession]
 *
 * @return A legacy [AirwallexSession] object representing the current session state
 * @throws IllegalArgumentException if required properties are missing
 */
suspend fun Session.convertToLegacySession(): AirwallexSession {
    // Ensure payment intent exists before conversion
    val paymentIntent = resolvePaymentIntentSuspend()
    val consentOptions = paymentConsentOptions

    return when {
        consentOptions == null -> {
            // One-off payment → AirwallexPaymentSession
            // Use the resolved paymentIntent (not this.paymentIntent)
            AirwallexPaymentSession.Builder(
                paymentIntent = paymentIntent,
                countryCode = countryCode,
                googlePayOptions = googlePayOptions
            ).apply {
                setRequireBillingInformation(isBillingInformationRequired)
                setRequireEmail(isEmailRequired)
                returnUrl?.let { setReturnUrl(it) }
                setAutoCapture(autoCapture)
                setHidePaymentConsents(hidePaymentConsents)
                paymentMethods?.let { setPaymentMethods(it) }
                shipping?.let { setShipping(it) }
            }.build()
        }

        paymentIntent.amount.compareTo(java.math.BigDecimal.ZERO) == 0 -> {
            // Recurring without intent → AirwallexRecurringSession
            // Use paymentIntent.clientSecret from the resolved intent
            AirwallexRecurringSession.Builder(
                customerId = requireNotNull(customerId) { "CustomerId required for recurring session" },
                clientSecret = requireNotNull(paymentIntent.clientSecret) { "ClientSecret required for recurring session" },
                currency = currency,
                amount = amount,
                nextTriggerBy = consentOptions.nextTriggeredBy,
                countryCode = countryCode,
            ).apply {
                setMerchantTriggerReason(
                    consentOptions.merchantTriggerReason
                        ?: PaymentConsent.MerchantTriggerReason.UNSCHEDULED
                )
                setRequireBillingInformation(isBillingInformationRequired)
                setRequireEmail(isEmailRequired)
                setAutoCapture(autoCapture)
                shipping?.let { setShipping(it) }
                consentOptions.merchantTriggerReason?.let { setMerchantTriggerReason(it) }
                googlePayOptions?.let { setGooglePayOptions(it) }
                returnUrl?.let { setReturnUrl(it) }
                paymentMethods?.let { setPaymentMethods(it) }
            }.build()
        }

        else -> {
            // Recurring with intent → AirwallexRecurringWithIntentSession
            // Use the resolved paymentIntent (not this.paymentIntent or provider)
            AirwallexRecurringWithIntentSession.Builder(
                paymentIntent = paymentIntent,
                customerId = requireNotNull(customerId) { "CustomerId required for recurring with intent" },
                nextTriggerBy = consentOptions.nextTriggeredBy,
                countryCode = countryCode
            ).apply {
                setRequireBillingInformation(isBillingInformationRequired)
                setRequireEmail(isEmailRequired)
                setMerchantTriggerReason(
                    consentOptions.merchantTriggerReason
                        ?: PaymentConsent.MerchantTriggerReason.UNSCHEDULED
                )
                returnUrl?.let { setReturnUrl(it) }
                setAutoCapture(autoCapture)
                googlePayOptions?.let { setGooglePayOptions(it) }
                paymentMethods?.let { setPaymentMethods(it) }
                shipping?.let { setShipping(it) }
            }.build()
        }
    }
}

/**
 * Helper method to resolve payment intent synchronously from a suspend function.
 * This is needed for the conversion logic.
 */
private suspend fun Session.resolvePaymentIntentSuspend(): com.airwallex.android.core.model.PaymentIntent {
    return kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
        (this as AirwallexSession).resolvePaymentIntent(object :
            PaymentIntentProvider.PaymentIntentCallback {
            override fun onSuccess(paymentIntent: com.airwallex.android.core.model.PaymentIntent) {
                continuation.resume(paymentIntent) {
                    // No cleanup needed
                }
            }

            override fun onError(error: Throwable) {
                continuation.cancel(error as? Exception ?: Exception(error.message, error))
            }
        })
    }
}
