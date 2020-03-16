package com.airwallex.android.view

import com.airwallex.android.Airwallex
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentMethod

internal abstract class AirwallexCheckoutBaseActivity : AirwallexActivity() {

    abstract val airwallex: Airwallex

    abstract val paymentIntent: PaymentIntent

    abstract val requestThreeDSecure: Boolean

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
            activity = this,
            paymentIntentId = paymentIntent.id,
            customerId = requireNotNull(paymentIntent.customerId),
            paymentMethod = paymentMethod,
            cvc = cvc,
            requestThreeDSecure = requestThreeDSecure,
            listener = callback
        )
    }
}
