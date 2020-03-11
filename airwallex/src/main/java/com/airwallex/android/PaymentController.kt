package com.airwallex.android

import com.airwallex.android.model.*

internal interface PaymentController {

    /**
     * Confirm the Airwallex PaymentIntent
     */
    fun confirmPaymentIntent(
        options: AirwallexApiRepository.Options,
        paymentIntentParams: PaymentIntentParams,
        callback: Airwallex.PaymentCallback<PaymentIntent>
    )

    /**
     * Retrieve the Airwallex Payment Intent
     */
    fun retrievePaymentIntent(
        options: AirwallexApiRepository.Options,
        callback: Airwallex.PaymentCallback<PaymentIntent>
    )

    /**
     * Create the Airwallex PaymentMethod
     */
    fun createPaymentMethod(
        options: AirwallexApiRepository.Options,
        paymentMethodParams: PaymentMethodParams,
        callback: Airwallex.PaymentCallback<PaymentMethod>
    )

    /**
     * Get all of customer's PaymentMethods
     */
    fun getPaymentMethods(
        options: AirwallexApiRepository.Options,
        callback: Airwallex.PaymentCallback<PaymentMethodResponse>
    )
}
