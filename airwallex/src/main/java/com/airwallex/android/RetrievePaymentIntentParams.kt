package com.airwallex.android

import com.airwallex.android.model.PaymentIntent

data class RetrievePaymentIntentParams(

    /**
     * ID of [PaymentIntent]
     */
    val paymentIntentId: String,

    /**
     * The client secret of [PaymentIntent]
     */
    val clientSecret: String
)