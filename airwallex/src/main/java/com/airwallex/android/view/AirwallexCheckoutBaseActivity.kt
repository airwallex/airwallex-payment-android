package com.airwallex.android.view

import com.airwallex.android.Airwallex
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentMethod
import kotlinx.android.synthetic.main.activity_payment_checkout.*

internal abstract class AirwallexCheckoutBaseActivity : AirwallexActivity() {

    abstract val airwallex: Airwallex

    abstract val paymentIntent: PaymentIntent

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
            cvc = requireNotNull(paymentMethodItemView.cvc),
            listener = callback
        )
    }
}
