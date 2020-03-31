package com.airwallex.android

import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.PaymentIntent

data class ConfirmPaymentIntentParams(

    /**
     * ID of [PaymentIntent]
     */
    val paymentIntentId: String,

    /**
     * The client secret of [PaymentIntent]
     */
    val clientSecret: String,

    /**
     * optional, the ID of a Customer.
     */
    val customerId: String?
) {
    class Builder(
        private val paymentIntentId: String,
        private val clientSecret: String
    ) : ObjectBuilder<ConfirmPaymentIntentParams> {

        private var customerId: String? = null

        fun setCustomerId(customerId: String?): Builder = apply {
            this.customerId = customerId
        }

        override fun build(): ConfirmPaymentIntentParams {
            return ConfirmPaymentIntentParams(
                paymentIntentId = paymentIntentId,
                clientSecret = clientSecret,
                customerId = customerId
            )
        }
    }
}