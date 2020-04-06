package com.airwallex.android

import com.airwallex.android.model.*

internal interface PaymentManager {

    /**
     * Confirm the [PaymentIntent] using [ApiRepository.Options]
     */
    fun confirmPaymentIntent(
        options: ApiRepository.Options,
        listener: Airwallex.PaymentListener<PaymentIntent>
    )

    /**
     * Retrieve the [PaymentIntent] using [ApiRepository.Options]
     */
    fun retrievePaymentIntent(
        options: ApiRepository.Options,
        listener: Airwallex.PaymentListener<PaymentIntent>
    )

    /**
     * Create a Airwallex [PaymentMethod]
     */
    fun createPaymentMethod(
        options: ApiRepository.Options,
        listener: Airwallex.PaymentListener<PaymentMethod>
    )

    /**
     * Retrieve all of the customer's [PaymentMethod]
     */
    fun retrievePaymentMethods(
        options: ApiRepository.Options,
        listener: Airwallex.PaymentListener<PaymentMethodResponse>
    )
}
