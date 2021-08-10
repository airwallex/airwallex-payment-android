package com.airwallex.android.core.model

/**
 * The params that used for continue [PaymentIntent]
 */
data class ContinuePaymentIntentParams(
    override val paymentIntentId: String,
    override val clientSecret: String,

    val type: PaymentIntentContinueType,
    val threeDSecure: ThreeDSecure? = null,
    val device: Device? = null,
    val useDcc: Boolean? = null
) : AbstractPaymentIntentParams(paymentIntentId = paymentIntentId, clientSecret = clientSecret)
