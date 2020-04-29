package com.airwallex.android.view

import com.airwallex.android.Airwallex
import com.airwallex.android.ConfirmPaymentIntentParams
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.PaymentMethodReference
import com.airwallex.android.model.PaymentMethodType

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
        setLoadingProgress(loading = true, cancelable = false)

        val params = when (paymentMethod.type) {
            PaymentMethodType.WECHAT -> {
                ConfirmPaymentIntentParams.createWeChatParams(
                    paymentIntentId = paymentIntent.id,
                    clientSecret = paymentIntent.clientSecret,
                    customerId = paymentIntent.customerId
                )
            }
            PaymentMethodType.CARD -> {
                ConfirmPaymentIntentParams.createCardParams(
                    paymentIntentId = paymentIntent.id,
                    clientSecret = paymentIntent.clientSecret,
                    paymentMethodReference = PaymentMethodReference(
                        requireNotNull(paymentMethod.id),
                        requireNotNull(cvc)
                    ),
                    customerId = paymentIntent.customerId
                )
            }
        }
        airwallex.confirmPaymentIntent(this, params, callback)
    }
}
