package com.airwallex.android

import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentMethodType

data class ConfirmPaymentIntentParams internal constructor(
    override val paymentIntentId: String,
    override val clientSecret: String,
    /**
     * optional, the ID of a Customer.
     */
    val customerId: String?,
    /**
     * Payment method type, default is WeChat
     */
    val paymentMethodType: PaymentMethodType = PaymentMethodType.WECHAT
) : AbstractPaymentIntentParams(paymentIntentId = paymentIntentId, clientSecret = clientSecret) {

    internal class Builder(
        private val paymentIntentId: String,
        private val clientSecret: String
    ) : ObjectBuilder<ConfirmPaymentIntentParams> {

        private var paymentMethodType: PaymentMethodType = PaymentMethodType.WECHAT
        private var customerId: String? = null

        internal fun setCustomerId(customerId: String?): Builder = apply {
            this.customerId = customerId
        }

        internal fun setPaymentMethodType(paymentMethodType: PaymentMethodType): Builder = apply {
            this.paymentMethodType = paymentMethodType
        }

        override fun build(): ConfirmPaymentIntentParams {
            return ConfirmPaymentIntentParams(
                paymentIntentId = paymentIntentId,
                clientSecret = clientSecret,
                customerId = customerId
            )
        }
    }

    companion object {
        /**
         * Return the [ConfirmPaymentIntentParams] for WeChat Pay
         *
         * @param paymentIntentId the ID of the [PaymentIntent], required.
         * @param clientSecret the clientSecret of [PaymentIntent], required.
         * @param customerId Customer who intends to pay for the payment intent, optional.
         */
        fun createWeChatParams(
            paymentIntentId: String,
            clientSecret: String,
            customerId: String? = null
        ): ConfirmPaymentIntentParams {
            return Builder(
                paymentIntentId = paymentIntentId,
                clientSecret = clientSecret
            )
                .setCustomerId(customerId)
                .setPaymentMethodType(PaymentMethodType.WECHAT)
                .build()
        }
    }
}
