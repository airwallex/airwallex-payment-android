package com.airwallex.android.view

import com.airwallex.android.Airwallex
import com.airwallex.android.DeviceUtils
import com.airwallex.android.model.*
import java.util.*
import kotlinx.android.synthetic.main.activity_payment_checkout.*

internal abstract class AirwallexCheckoutBaseActivity : AirwallexActivity() {

    abstract val airwallex: Airwallex

    abstract val paymentIntent: PaymentIntent

    override fun onActionSave() {
        // Ignore
    }

    private fun buildPaymentIntentParams(
        paymentMethod: PaymentMethod,
        customerId: String?
    ): PaymentIntentParams {
        return when (paymentMethod.type) {
            PaymentMethodType.CARD -> {
                PaymentIntentParams.Builder()
                    .setRequestId(UUID.randomUUID().toString())
                    .setCustomerId(customerId)
                    .setDevice(DeviceUtils.device)
                    .setPaymentMethodReference(
                        PaymentMethodReference.Builder()
                            .setId(paymentMethod.id)
                            .setCvc(paymentMethodItemView.cvc)
                            .build()
                    )
                    .setPaymentMethodOptions(
                        PaymentMethodOptions.Builder()
                            .setCardOptions(
                                PaymentMethodOptions.CardOptions.Builder()
                                    .setAutoCapture(true)
                                    .setThreeDs(
                                        PaymentMethodOptions.CardOptions.ThreeDs.Builder()
                                            .build()
                                    ).build()
                            )
                            .build()
                    )
                    .build()
            }
            PaymentMethodType.WECHAT -> {
                PaymentIntentParams.Builder()
                    .setRequestId(UUID.randomUUID().toString())
                    .setCustomerId(customerId)
                    .setDevice(DeviceUtils.device)
                    .setPaymentMethod(paymentMethod)
                    .build()
            }
        }
    }

    protected fun confirmPaymentIntent(
        paymentMethod: PaymentMethod,
        callback: Airwallex.PaymentListener<PaymentIntent>
    ) {
        setLoadingProgress(true)
        airwallex.confirmPaymentIntent(
            activity = this,
            paymentMethodType = paymentMethod.type,
            paymentIntentId = paymentIntent.id,
            paymentIntentParams = buildPaymentIntentParams(paymentMethod, paymentIntent.customerId),
            listener = callback
        )
    }
}
