package com.airwallex.android

import com.airwallex.android.model.*

internal interface PaymentController {

    fun confirmPaymentIntent(
        options: AirwallexApiRepository.Options,
        paymentIntentParams: PaymentIntentParams,
        callback: Airwallex.PaymentCallback<PaymentIntent>
    )

    fun retrievePaymentIntent(
        options: AirwallexApiRepository.Options,
        callback: Airwallex.PaymentCallback<PaymentIntent>
    )

    fun createPaymentMethod(
        options: AirwallexApiRepository.Options,
        paymentMethodParams: PaymentMethodParams,
        callback: Airwallex.PaymentCallback<PaymentMethod>
    )

    fun getPaymentMethods(
        options: AirwallexApiRepository.Options,
        callback: Airwallex.PaymentCallback<PaymentMethodResponse>
    )
}
