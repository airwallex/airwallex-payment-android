package com.airwallex.android.view

import com.airwallex.android.Airwallex
import com.airwallex.android.ConfirmPaymentIntentParams
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentMethod

internal abstract class AirwallexCheckoutBaseActivity : AirwallexActivity() {

    abstract val airwallex: Airwallex

    abstract val paymentIntent: PaymentIntent

    abstract val cvc: String?

    override fun onActionSave() {
        // Ignore
    }

    protected fun confirmPaymentIntent(
        paymentMethod: PaymentMethod,
        callback: Airwallex.PaymentListener<PaymentIntent>
    ) {
        setLoadingProgress(true)
        airwallex.confirmPaymentIntent(
            params = ConfirmPaymentIntentParams.Builder(
                // the ID of the `PaymentIntent`, required.
                paymentIntentId = paymentIntent.id,
                // the clientSecret of `PaymentIntent`, required.
                clientSecret = paymentIntent.clientSecret
            )
                // the customerId of `PaymentIntent`, optional.
                .setCustomerId(paymentIntent.customerId)
                .build(),
            listener = callback
        )
    }
}
