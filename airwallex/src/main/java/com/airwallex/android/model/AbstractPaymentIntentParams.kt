package com.airwallex.android.model

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
