package com.airwallex.android.core.model

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
