package com.airwallex.android

/**
 * An interface for data operations on Airwallex API objects.
 */
internal interface ApiRepository {

    fun confirmPaymentIntent(
        options: AirwallexApiRepository.Options
    ): AirwallexHttpResponse?

    fun retrievePaymentIntent(
        options: AirwallexApiRepository.Options
    ): AirwallexHttpResponse?
}
