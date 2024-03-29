package com.airwallex.android.core.model

abstract class AbstractPaymentMethodParams(

    /**
     * ID of Customer
     */
    open val customerId: String,

    /**
     * The client secret of [PaymentIntent]
     */
    open val clientSecret: String
)
