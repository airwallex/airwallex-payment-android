package com.airwallex.android

internal interface ApiRepository {

    fun confirmPaymentIntent(options: AirwallexApiRepository.Options): PaymentIntent?

    fun retrievePaymentIntent(options: AirwallexApiRepository.Options): PaymentIntent?

}