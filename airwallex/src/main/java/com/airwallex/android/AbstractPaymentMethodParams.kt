package com.airwallex.android

import com.airwallex.android.model.PaymentIntent

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
