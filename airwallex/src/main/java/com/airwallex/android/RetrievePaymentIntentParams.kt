package com.airwallex.android

import com.airwallex.android.model.PaymentIntent

/**
 * The params that used for retrieve [PaymentIntent]
 */
data class RetrievePaymentIntentParams constructor(
    override val paymentIntentId: String,
    override val clientSecret: String
) : AbstractPaymentIntentParams(paymentIntentId = paymentIntentId, clientSecret = clientSecret)
