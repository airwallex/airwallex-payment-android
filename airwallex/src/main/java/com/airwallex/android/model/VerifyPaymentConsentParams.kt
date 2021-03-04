package com.airwallex.android.model

/**
 * The params that used for verify [PaymentConsent]
 */
data class VerifyPaymentConsentParams constructor(
    val clientSecret: String,
    val paymentConsentId: String,
    val paymentMethodType: PaymentMethodType? = null,
    val returnUrl: String?
)
