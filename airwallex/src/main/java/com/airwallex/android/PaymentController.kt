package com.airwallex.android

import com.airwallex.android.model.*

internal interface PaymentController {

    /**
     * Confirm the [PaymentIntent] using [AirwallexApiRepository.Options]
     */
    fun confirmPaymentIntent(
        options: AirwallexApiRepository.Options,
        listener: Airwallex.PaymentListener<PaymentIntent>
    )

    /**
     * Retrieve the [PaymentIntent] using [AirwallexApiRepository.Options]
     */
    fun retrievePaymentIntent(
        options: AirwallexApiRepository.Options,
        listener: Airwallex.PaymentListener<PaymentIntent>
    )
}
