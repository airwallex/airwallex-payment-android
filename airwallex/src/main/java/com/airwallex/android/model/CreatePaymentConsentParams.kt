package com.airwallex.android.model

/**
 * The params that used for create [PaymentConsent]
 */
data class CreatePaymentConsentParams constructor(
    val clientSecret: String,
    val customerId: String,
    val paymentMethodType: PaymentMethodType
)
