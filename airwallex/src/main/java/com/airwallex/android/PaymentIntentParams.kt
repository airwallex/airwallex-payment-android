package com.airwallex.android

import com.airwallex.android.model.PaymentIntent

open class PaymentIntentParams(

    /**
     * ID of [PaymentIntent]
     */
    open val paymentIntentId: String,

    /**
     * The client secret of [PaymentIntent]
     */
    open val clientSecret: String
)
