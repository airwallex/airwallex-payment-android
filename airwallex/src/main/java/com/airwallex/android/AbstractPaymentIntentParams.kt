package com.airwallex.android

import com.airwallex.android.model.PaymentIntent

abstract class AbstractPaymentIntentParams(

    /**
     * ID of [PaymentIntent]
     */
    open val paymentIntentId: String,

    /**
     * The client secret of [PaymentIntent]
     */
    open val clientSecret: String
)
