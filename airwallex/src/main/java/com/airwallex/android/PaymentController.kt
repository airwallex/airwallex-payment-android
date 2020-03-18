package com.airwallex.android

import com.airwallex.android.model.*

internal interface PaymentController {

    /**
     * Confirm the Airwallex PaymentIntent
     */
    fun confirmPaymentIntent(
        options: AirwallexApiRepository.Options,
        listener: Airwallex.PaymentListener<PaymentIntent>
    )

    /**
     * Retrieve the Airwallex Payment Intent
     */
    fun retrievePaymentIntent(
        options: AirwallexApiRepository.Options,
        listener: Airwallex.PaymentListener<PaymentIntent>
    )
}
