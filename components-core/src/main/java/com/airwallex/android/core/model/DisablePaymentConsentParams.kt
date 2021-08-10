package com.airwallex.android.core.model

/**
 * The params that used for disable [PaymentConsent]
 */
data class DisablePaymentConsentParams constructor(
    val paymentConsentId: String,
    val clientSecret: String
)
