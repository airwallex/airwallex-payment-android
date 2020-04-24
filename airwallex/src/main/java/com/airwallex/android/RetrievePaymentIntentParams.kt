package com.airwallex.android

data class RetrievePaymentIntentParams constructor(
    override val paymentIntentId: String,
    override val clientSecret: String
) : AbstractPaymentIntentParams(paymentIntentId = paymentIntentId, clientSecret = clientSecret)
