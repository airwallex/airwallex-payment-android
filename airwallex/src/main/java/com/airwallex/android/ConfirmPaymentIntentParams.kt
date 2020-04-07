package com.airwallex.android

import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.PaymentIntent
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
     * [PaymentMethodReference] used to confirm [PaymentIntent].
     * When [paymentMethodType] is [PaymentMethodType.CARD], it's should be not null
     * When [paymentMethodType] is [PaymentMethodType.WECHAT], it's should be null
     */
    val paymentMethodReference: PaymentMethodReference? = null,

    /**
     * Payment method type, default is [PaymentMethodType.WECHAT]
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

        fun setPaymentMethod(
            paymentMethodType: PaymentMethodType,
            paymentMethodReference: PaymentMethodReference? = null
        ): Builder = apply {
            this.paymentMethodType = paymentMethodType
            if (paymentMethodType == PaymentMethodType.CARD) {
                this.paymentMethodReference = requireNotNull(paymentMethodReference)
            }
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
