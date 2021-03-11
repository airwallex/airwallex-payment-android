package com.airwallex.android.model

/**
 * The params that used for disable [PaymentConsent]
 */
data class DisablePaymentConsentParams constructor(
    val paymentConsentId: String,
    val clientSecret: String
)
