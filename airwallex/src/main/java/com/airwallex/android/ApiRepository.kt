package com.airwallex.android

import com.airwallex.android.model.PaymentIntentConfirmRequest

/**
 * An interface for data operations on Airwallex API objects.
 */
internal interface ApiRepository {

    fun confirmPaymentIntent(
        options: AirwallexApiRepository.Options,
        paymentIntentParams: PaymentIntentConfirmRequest
    ): AirwallexHttpResponse?

    fun retrievePaymentIntent(
        options: AirwallexApiRepository.Options
    ): AirwallexHttpResponse?
}
