package com.airwallex.android

import com.airwallex.android.model.PaymentIntentParams

internal interface PaymentController {

    fun startConfirm(options: AirwallexApiRepository.Options, paymentIntentParams: PaymentIntentParams, callback: Airwallex.PaymentIntentCallback)

    fun retrievePaymentIntent(options: AirwallexApiRepository.Options, callback: Airwallex.PaymentIntentCallback)
}
