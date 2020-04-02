package com.airwallex.android

import com.airwallex.android.model.ObjectBuilder

data class ConfirmPaymentIntentParams internal constructor(
    val type: ConfirmPaymentIntentType,
    override val paymentIntentId: String,
    override val clientSecret: String,
    /**
     * optional, the ID of a Customer.
     */
    val customerId: String?
) : AbstractPaymentIntentParams(paymentIntentId = paymentIntentId, clientSecret = clientSecret) {

    class Builder(
        private val type: ConfirmPaymentIntentType = ConfirmPaymentIntentType.WECHAT,
        private val paymentIntentId: String,
        private val clientSecret: String
    ) : ObjectBuilder<ConfirmPaymentIntentParams> {

        private var customerId: String? = null

        fun setCustomerId(customerId: String?): Builder = apply {
            this.customerId = customerId
        }

        override fun build(): ConfirmPaymentIntentParams {
            return ConfirmPaymentIntentParams(
                type = type,
                paymentIntentId = paymentIntentId,
                clientSecret = clientSecret,
                customerId = customerId
            )
        }
    }

    enum class ConfirmPaymentIntentType {
        WECHAT
    }
}
