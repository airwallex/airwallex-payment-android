package com.airwallex.android.model

/**
 * The params that used for retrieve [PaymentConsent]
 */
data class RetrievePaymentConsentParams constructor(
    val paymentConsentId: String,
    val clientSecret: String
)
