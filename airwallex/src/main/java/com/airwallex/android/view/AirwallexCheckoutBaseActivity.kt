package com.airwallex.android.view

import com.airwallex.android.Airwallex
import com.airwallex.android.model.ConfirmPaymentIntentParams
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentMethod
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
        listener: Airwallex.PaymentListener<PaymentIntent>
    ) {
        setLoadingProgress(loading = true, cancelable = false)
        val params = when (requireNotNull(paymentMethod.type)) {
            PaymentMethodType.WECHAT -> {
                ConfirmPaymentIntentParams.createWeChatParams(
                    paymentIntentId = paymentIntent.id,
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    customerId = paymentIntent.customerId
                )
            }
            PaymentMethodType.ALIPAY_CN -> {
                ConfirmPaymentIntentParams.createAlipayParams(
                    paymentIntentId = paymentIntent.id,
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    customerId = paymentIntent.customerId
                )
            }
            PaymentMethodType.ALIPAY_HK -> {
                ConfirmPaymentIntentParams.createAlipayHKParams(
                    paymentIntentId = paymentIntent.id,
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    customerId = paymentIntent.customerId
                )
            }
            PaymentMethodType.DANA -> {
                ConfirmPaymentIntentParams.createDanaParams(
                    paymentIntentId = paymentIntent.id,
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    customerId = paymentIntent.customerId
                )
            }
            PaymentMethodType.GCASH -> {
                ConfirmPaymentIntentParams.createGCashParams(
                    paymentIntentId = paymentIntent.id,
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    customerId = paymentIntent.customerId
                )
            }
            PaymentMethodType.KAKAOPAY -> {
                ConfirmPaymentIntentParams.createKakaoParams(
                    paymentIntentId = paymentIntent.id,
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    customerId = paymentIntent.customerId
                )
            }
            PaymentMethodType.TNG -> {
                ConfirmPaymentIntentParams.createTngParams(
                    paymentIntentId = paymentIntent.id,
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    customerId = paymentIntent.customerId
                )
            }
            PaymentMethodType.VISA, PaymentMethodType.MASTERCARD -> {
                ConfirmPaymentIntentParams.createCardParams(
                    paymentIntentId = paymentIntent.id,
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    paymentMethodId = requireNotNull(paymentMethod.id),
                    cvc = requireNotNull(cvc),
                    customerId = paymentIntent.customerId
                )
            }
        }
        airwallex.confirmPaymentIntent(params, listener)
    }
}
