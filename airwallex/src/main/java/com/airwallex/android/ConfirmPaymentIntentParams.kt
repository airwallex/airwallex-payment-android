package com.airwallex.android

import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.PaymentMethod

data class ConfirmPaymentIntentParams internal constructor(
    override val paymentIntentId: String,
    override val clientSecret: String,
    /**
     * optional, the ID of a Customer.
     */
    val customerId: String?,
    /**
     * ID of the [PaymentMethod]
     */
    val paymentMethodId: String?
) : AbstractPaymentIntentParams(paymentIntentId = paymentIntentId, clientSecret = clientSecret) {

    class Builder(
        private val paymentIntentId: String,
        private val clientSecret: String
    ) : ObjectBuilder<ConfirmPaymentIntentParams> {

        private var customerId: String? = null

        private var paymentMethodId: String? = null

        fun setCustomerId(customerId: String?): Builder = apply {
            this.customerId = customerId
        }

        fun setPaymentMethodId(paymentMethodId: String?): Builder = apply {
            this.paymentMethodId = paymentMethodId
        }

        override fun build(): ConfirmPaymentIntentParams {
            return ConfirmPaymentIntentParams(
                paymentIntentId = paymentIntentId,
                clientSecret = clientSecret,
                customerId = customerId,
                paymentMethodId = paymentMethodId
            )
        }
    }
}
