package com.airwallex.android

import com.airwallex.android.model.PaymentIntentParams
import com.airwallex.android.model.PaymentMethodParams

internal interface PaymentController {

    fun confirmPaymentIntent(
        options: AirwallexApiRepository.Options,
        paymentIntentParams: PaymentIntentParams,
        callback: Airwallex.PaymentIntentCallback
    )

    fun retrievePaymentIntent(
        options: AirwallexApiRepository.Options,
        callback: Airwallex.PaymentIntentCallback
    )

    fun createPaymentMethod(
        options: AirwallexApiRepository.Options,
        paymentMethodParams: PaymentMethodParams,
        callback: Airwallex.PaymentMethodCallback
    )
}
