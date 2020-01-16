package com.airwallex.android

internal interface AirwallexRepository {

    fun confirmPaymentIntent(token: String, paymentIntentId: String): PaymentIntent?

    fun retrievePaymentIntent(
    ): PaymentIntent?

}