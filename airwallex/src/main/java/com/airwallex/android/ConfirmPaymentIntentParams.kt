package com.airwallex.android

import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.PaymentMethodReference
import com.airwallex.android.model.PaymentMethodType

data class ConfirmPaymentIntentParams internal constructor(
    override val paymentIntentId: String,
    override val clientSecret: String,
    /**
     * optional, the ID of a Customer.
     */
    val customerId: String?,

    /**
     * The object of [PaymentMethodReference], should be supported when [paymentMethodType] is Card
     */
    val paymentMethodReference: PaymentMethodReference? = null,

    /**
     * Payment method type, default is WeChat
     */
    val paymentMethodType: PaymentMethodType = PaymentMethodType.WECHAT
) : AbstractPaymentIntentParams(paymentIntentId = paymentIntentId, clientSecret = clientSecret) {

    class Builder(
        private val paymentIntentId: String,
        private val clientSecret: String
    ) : ObjectBuilder<ConfirmPaymentIntentParams> {

        private var paymentMethodType: PaymentMethodType = PaymentMethodType.WECHAT
        private var customerId: String? = null
        private var paymentMethodReference: PaymentMethodReference? = null

        fun setCustomerId(customerId: String?): Builder = apply {
            this.customerId = customerId
        }

        fun setPaymentMethodType(paymentMethodType: PaymentMethodType): Builder = apply {
            this.paymentMethodType = paymentMethodType
        }

        fun setPaymentMethodReference(paymentMethodReference: PaymentMethodReference?): Builder = apply {
            this.paymentMethodReference = paymentMethodReference
        }

        override fun build(): ConfirmPaymentIntentParams {
            return ConfirmPaymentIntentParams(
                paymentIntentId = paymentIntentId,
                clientSecret = clientSecret,
                customerId = customerId,
                paymentMethodReference = paymentMethodReference,
                paymentMethodType = paymentMethodType
            )
        }
    }
}
