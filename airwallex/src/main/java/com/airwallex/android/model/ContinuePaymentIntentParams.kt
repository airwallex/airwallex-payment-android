package com.airwallex.android.model

/**
 * The params that used for continue [PaymentIntent]
 */
internal data class ContinuePaymentIntentParams internal constructor(
    override val paymentIntentId: String,
    override val clientSecret: String,

    val type: PaymentIntentContinueType,
    val threeDSecure: ThreeDSecure? = null,
    val device: Device? = null,
    val useDcc: Boolean? = null
) : AbstractPaymentIntentParams(paymentIntentId = paymentIntentId, clientSecret = clientSecret)
