package com.airwallex.android.view

import com.airwallex.android.Airwallex
import com.airwallex.android.ConfirmPaymentIntentParams
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.PaymentMethodReference
import com.airwallex.android.model.PaymentMethodType

abstract class AirwallexCheckoutBaseActivity : AirwallexActivity() {

    abstract val airwallex: Airwallex

    abstract val paymentIntent: PaymentIntent

    abstract val cvc: String?

    override fun onActionSave() {
        // Ignore
    }

    protected fun confirmPaymentIntent(
        paymentMethod: PaymentMethod,
        listener: Airwallex.PaymentListener<PaymentIntent>
    ) {
        setLoadingProgress(loading = true, cancelable = false)
        if (paymentMethod.type == PaymentMethodType.WECHAT) {
            val params = ConfirmPaymentIntentParams.createWeChatParams(
                paymentIntentId = paymentIntent.id,
                clientSecret = requireNotNull(paymentIntent.clientSecret),
                customerId = paymentIntent.customerId
            )
            airwallex.confirmPaymentIntent(this, params, listener)
        } else if (paymentMethod.type == PaymentMethodType.CARD) {
            val params = ConfirmPaymentIntentParams.createCardParams(
                paymentIntentId = paymentIntent.id,
                clientSecret = requireNotNull(paymentIntent.clientSecret),
                paymentMethodReference = PaymentMethodReference(
                    requireNotNull(paymentMethod.id),
                    requireNotNull(cvc)
                ),
                customerId = paymentIntent.customerId
            )
            airwallex.confirmPaymentIntent(this, params, listener)
        }
    }
}
