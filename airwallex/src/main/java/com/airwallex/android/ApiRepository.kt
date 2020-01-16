package com.airwallex.android

internal interface ApiRepository {

    fun confirmPaymentIntent(token: String, paymentIntentId: String): PaymentIntent?

    fun retrievePaymentIntent(token: String, paymentIntentId: String): PaymentIntent?

}