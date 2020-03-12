package com.airwallex.android

import com.airwallex.android.model.*

internal interface PaymentController {

    /**
     * Confirm the Airwallex PaymentIntent
     */
    fun confirmPaymentIntent(
        options: AirwallexApiRepository.Options,
        paymentIntentParams: PaymentIntentParams,
        listener: Airwallex.PaymentListener<PaymentIntent>
    )

    /**
     * Retrieve the Airwallex Payment Intent
     */
    fun retrievePaymentIntent(
        options: AirwallexApiRepository.Options,
        listener: Airwallex.PaymentListener<PaymentIntent>
    )

    /**
     * Create the Airwallex PaymentMethod
     */
    fun createPaymentMethod(
        options: AirwallexApiRepository.Options,
        paymentMethodParams: PaymentMethodParams,
        listener: Airwallex.PaymentListener<PaymentMethod>
    )

    /**
     * Get all of customer's PaymentMethods
     */
    fun getPaymentMethods(
        options: AirwallexApiRepository.Options,
        listener: Airwallex.PaymentListener<PaymentMethodResponse>
    )
}
