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
}
