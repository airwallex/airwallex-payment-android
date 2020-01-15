package com.airwallex.android

internal interface AirwallexRepository {

    fun confirmPaymentIntent(
        confirmPaymentIntentParams: ConfirmPaymentIntentParams,
        options: ApiRequest.Options
    ): PaymentIntent?

    fun retrievePaymentIntent(
        options: ApiRequest.Options
    ): PaymentIntent?

}