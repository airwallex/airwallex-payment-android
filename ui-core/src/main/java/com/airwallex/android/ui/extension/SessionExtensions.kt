package com.airwallex.android.ui.extension

import com.airwallex.android.core.ParcelableSession
import com.airwallex.android.core.Session

fun Session.toParcelableSession(): ParcelableSession {
    return ParcelableSession(
        paymentIntent = paymentIntent,
        paymentIntentProviderId = paymentIntentProviderId,
        paymentConsentOptions = paymentConsentOptions,
        currency = currency,
        countryCode = countryCode,
        amount = amount,
        shipping = shipping,
        isBillingInformationRequired = isBillingInformationRequired,
        isEmailRequired = isEmailRequired,
        customerId = customerId,
        returnUrl = returnUrl,
        googlePayOptions = googlePayOptions,
        paymentMethods = paymentMethods,
        autoCapture = autoCapture,
        hidePaymentConsents = hidePaymentConsents
    )
}
