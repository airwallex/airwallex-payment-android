package com.airwallex.android

import com.airwallex.android.model.Device
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentIntentContinueType

/**
 * The params that used for continue [PaymentIntent]
 */
data class ContinuePaymentIntentParams internal constructor(
    override val paymentIntentId: String,
    override val clientSecret: String,

    val type: PaymentIntentContinueType,
    val threeDSecure: com.airwallex.android.model.ThreeDSecure? = null,
    val device: Device? = null,
    val useDcc: Boolean? = null
) : AbstractPaymentIntentParams(paymentIntentId = paymentIntentId, clientSecret = clientSecret)
