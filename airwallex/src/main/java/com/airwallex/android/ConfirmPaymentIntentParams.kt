package com.airwallex.android

import com.airwallex.android.model.ObjectBuilder

data class ConfirmPaymentIntentParams internal constructor(
    val type: ConfirmPaymentIntentType,
    override val paymentIntentId: String,
    override val clientSecret: String,
    /**
     * optional, the ID of a Customer.
     */
    val customerId: String?,
    val paymentMethodId: String?,
    val cvc: String?
) : AbstractPaymentIntentParams(paymentIntentId = paymentIntentId, clientSecret = clientSecret) {

    class Builder(
        private val type: ConfirmPaymentIntentType = ConfirmPaymentIntentType.WECHAT,
        private val paymentIntentId: String,
        private val clientSecret: String
    ) : ObjectBuilder<ConfirmPaymentIntentParams> {

        private var customerId: String? = null
        private var paymentMethodId: String? = null
        private var cvc: String? = null

        fun setCustomerId(customerId: String?): Builder = apply {
            this.customerId = customerId
        }

        fun setPaymentMethodId(paymentMethodId: String?): Builder = apply {
            this.paymentMethodId = paymentMethodId
        }

        fun setCvc(cvc: String?): Builder = apply {
            this.cvc = cvc
        }

        override fun build(): ConfirmPaymentIntentParams {
            return ConfirmPaymentIntentParams(
                type = type,
                paymentIntentId = paymentIntentId,
                clientSecret = clientSecret,
                customerId = customerId,
                paymentMethodId = paymentMethodId,
                cvc = cvc
            )
        }
    }

    enum class ConfirmPaymentIntentType {
        WECHAT, CARD
    }
}
