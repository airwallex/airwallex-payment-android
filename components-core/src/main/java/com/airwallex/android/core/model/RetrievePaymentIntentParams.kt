package com.airwallex.android.core.model

/**
 * The params that used for retrieve [PaymentIntent]
 */
data class RetrievePaymentIntentParams constructor(
    override val paymentIntentId: String,
    override val clientSecret: String
) : AbstractPaymentIntentParams(paymentIntentId = paymentIntentId, clientSecret = clientSecret)
