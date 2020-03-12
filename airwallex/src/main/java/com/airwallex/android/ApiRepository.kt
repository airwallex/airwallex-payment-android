package com.airwallex.android

import com.airwallex.android.model.PaymentIntentParams
import com.airwallex.android.model.PaymentMethodParams

/**
 * An interface for data operations on Airwallex API objects.
 */
internal interface ApiRepository {

    fun confirmPaymentIntent(
        options: AirwallexApiRepository.Options,
        paymentIntentParams: PaymentIntentParams
    ): AirwallexHttpResponse?

    fun retrievePaymentIntent(
        options: AirwallexApiRepository.Options
    ): AirwallexHttpResponse?

    fun createPaymentMethod(
        options: AirwallexApiRepository.Options,
        paymentMethodParams: PaymentMethodParams
    ): AirwallexHttpResponse?

    fun getPaymentMethods(
        options: AirwallexApiRepository.Options
    ): AirwallexHttpResponse?
}
