package com.airwallex.android.core.model

/**
 * The params that used for retrieve [PaymentConsent]
 */
data class RetrievePaymentConsentParams constructor(
    val paymentConsentId: String,
    val clientSecret: String
)
