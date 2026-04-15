package com.airwallex.android.core.extension

import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.AirwallexRecurringSession
import com.airwallex.android.core.AirwallexRecurringWithIntentSession
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.PaymentIntentProvider
import com.airwallex.android.core.PaymentIntentResolvableSession
import com.airwallex.android.core.Session
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentConsentOptions
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.resolvePaymentIntent

/**
 * Converts the current [Session] instance to a legacy [AirwallexSession] object.
 *
 * This conversion is primarily required for Local Payment Methods (LPM),
 * as they are not yet supported by the unified Session flow.
 *
 * Determines which legacy session to create based on Session properties:
 * - `paymentConsentOptions == null` → [AirwallexPaymentSession] (one-off)
 * - `paymentConsentOptions != null && amount == 0` → [AirwallexRecurringSession]
 * - `paymentConsentOptions != null && amount > 0` → [AirwallexRecurringWithIntentSession]
 *
 * Preserves PaymentIntentProvider for Express Checkout scenarios where applicable.
 *
 * @return A legacy [AirwallexSession] object representing the current session state
 * @throws IllegalArgumentException if required properties are missing
 */
suspend fun Session.convertToLegacySession(): AirwallexSession {
    val consentOptions = paymentConsentOptions

    return when {
        consentOptions == null -> {
            oneOffPaymentSession()
        }

        amount.compareTo(java.math.BigDecimal.ZERO) == 0 -> {
            recurringSession(consentOptions)
        }

        else -> {
            recurringWithIntentSession(consentOptions)
        }
    }
}

private suspend fun Session.recurringWithIntentSession(
    consentOptions: PaymentConsentOptions
): AirwallexRecurringWithIntentSession {
    // Preserve provider if Session was created with one (Express Checkout)
    val provider = (this as? PaymentIntentResolvableSession)?.paymentIntentProvider

    // Only resolve PaymentIntent if provider is not available
    val paymentIntent = if (provider == null) {
        resolvePaymentIntentSuspend()
    } else {
        null
    }

    val builder = if (provider != null) {
        // Use provider constructor for Express Checkout sessions
        AirwallexRecurringWithIntentSession.Builder(
            paymentIntentProvider = provider,
            customerId = requireNotNull(customerId) { "CustomerId required for recurring with intent" },
            nextTriggerBy = consentOptions.nextTriggeredBy,
            countryCode = countryCode
        )
    } else {
        // Use PaymentIntent constructor for traditional sessions
        AirwallexRecurringWithIntentSession.Builder(
            paymentIntent = requireNotNull(paymentIntent) { "PaymentIntent required when provider is null" },
            customerId = requireNotNull(customerId) { "CustomerId required for recurring with intent" },
            nextTriggerBy = consentOptions.nextTriggeredBy,
            countryCode = countryCode
        )
    }

    return builder.apply {
        setRequireBillingInformation(isBillingInformationRequired)
        setRequireEmail(isEmailRequired)
        setMerchantTriggerReason(
            consentOptions.merchantTriggerReason
                ?: PaymentConsent.MerchantTriggerReason.UNSCHEDULED
        )
        returnUrl?.let { setReturnUrl(it) }
        setAutoCapture(autoCapture)
        setHidePaymentConsents(hidePaymentConsents)
        googlePayOptions?.let { setGooglePayOptions(it) }
        paymentMethods?.let { setPaymentMethods(it) }
        shipping?.let { setShipping(it) }
    }.build()
}

private suspend fun Session.recurringSession(
    consentOptions: PaymentConsentOptions
): AirwallexRecurringSession {
    val paymentIntent = resolvePaymentIntentSuspend()
    return AirwallexRecurringSession.Builder(
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
        googlePayOptions?.let { setGooglePayOptions(it) }
        returnUrl?.let { setReturnUrl(it) }
        paymentMethods?.let { setPaymentMethods(it) }
    }.build()
}

private suspend fun Session.oneOffPaymentSession(): AirwallexPaymentSession {
    // Preserve provider if Session was created with one (Express Checkout)
    val provider = (this as? PaymentIntentResolvableSession)?.paymentIntentProvider

    // Only resolve PaymentIntent if provider is not available
    val paymentIntent = if (provider == null) {
        resolvePaymentIntentSuspend()
    } else {
        null
    }

    val builder = if (provider != null) {
        // Use provider constructor for Express Checkout sessions
        AirwallexPaymentSession.Builder(
            paymentIntentProvider = provider,
            countryCode = countryCode,
            customerId = customerId,
            googlePayOptions = googlePayOptions
        )
    } else {
        // Use PaymentIntent constructor for traditional sessions
        AirwallexPaymentSession.Builder(
            paymentIntent = requireNotNull(paymentIntent) { "PaymentIntent required when provider is null" },
            countryCode = countryCode,
            googlePayOptions = googlePayOptions
        )
    }

    return builder.apply {
        setRequireBillingInformation(isBillingInformationRequired)
        setRequireEmail(isEmailRequired)
        returnUrl?.let { setReturnUrl(it) }
        setAutoCapture(autoCapture)
        setHidePaymentConsents(hidePaymentConsents)
        paymentMethods?.let { setPaymentMethods(it) }
        shipping?.let { setShipping(it) }
    }.build()
}

/**
 * Helper method to resolve payment intent synchronously from a suspend function.
 * This is needed for the conversion logic.
 */
private suspend fun Session.resolvePaymentIntentSuspend(): PaymentIntent {
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

/**
 * Converts [AirwallexPaymentSession] (legacy one-off payment session) to the new [Session] type.
 * Preserves both paymentIntent and paymentIntentProvider for full compatibility.
 *
 * @return A [Session] object representing the one-off payment
 */
fun AirwallexPaymentSession.convertToSession(): Session {
    return Session(
        paymentIntent = paymentIntent,
        paymentIntentProviderId = paymentIntentProviderId,
        paymentConsentOptions = null, // One-off payment has no consent options
        currency = currency,
        countryCode = countryCode,
        amount = amount,
        customerId = customerId,
        returnUrl = returnUrl,
        autoCapture = autoCapture,
        isBillingInformationRequired = isBillingInformationRequired,
        isEmailRequired = isEmailRequired,
        hidePaymentConsents = hidePaymentConsents,
        googlePayOptions = googlePayOptions,
        paymentMethods = paymentMethods,
        shipping = shipping
    ).also {
        // Preserve the transient provider field (not parceled, must be set manually)
        it.paymentIntentProvider = (this as? PaymentIntentResolvableSession)?.paymentIntentProvider
    }
}

/**
 * Converts [AirwallexRecurringWithIntentSession] (legacy recurring with intent session) to the new [Session] type.
 * Preserves both paymentIntent and paymentIntentProvider for full compatibility.
 *
 * @return A [Session] object representing the recurring payment with intent
 */
fun AirwallexRecurringWithIntentSession.convertToSession(): Session {
    return Session(
        paymentIntent = paymentIntent,
        paymentIntentProviderId = paymentIntentProviderId,
        paymentConsentOptions = PaymentConsentOptions(
            nextTriggeredBy = nextTriggerBy,
            merchantTriggerReason = merchantTriggerReason
        ),
        currency = currency,
        countryCode = countryCode,
        amount = amount,
        customerId = customerId,
        returnUrl = returnUrl,
        autoCapture = autoCapture,
        isBillingInformationRequired = isBillingInformationRequired,
        isEmailRequired = isEmailRequired,
        hidePaymentConsents = hidePaymentConsents,
        googlePayOptions = googlePayOptions,
        paymentMethods = paymentMethods,
        shipping = shipping
    ).also {
        // Preserve the transient provider field (not parceled, must be set manually)
        it.paymentIntentProvider = (this as? PaymentIntentResolvableSession)?.paymentIntentProvider
    }
}
