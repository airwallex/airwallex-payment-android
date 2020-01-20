package com.airwallex.android

import com.airwallex.android.model.PaymentIntentParams

internal interface ApiRepository {

    fun confirmPaymentIntent(options: AirwallexApiRepository.Options, paymentIntentParams: PaymentIntentParams): AirwallexHttpResponse?

    fun retrievePaymentIntent(options: AirwallexApiRepository.Options): AirwallexHttpResponse?

}