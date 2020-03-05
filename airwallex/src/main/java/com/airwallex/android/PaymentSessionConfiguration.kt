package com.airwallex.android

import android.os.Parcelable
import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.Shipping
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentSessionConfiguration(
    val shipping: Shipping?,
    val paymentIntent: PaymentIntent?,
    val token: String?,
    val paymentMethod: PaymentMethod?
) : Parcelable {

    class Builder : ObjectBuilder<PaymentSessionConfiguration> {

        private var shipping: Shipping? = null
        private var paymentIntent: PaymentIntent? = null
        private var token: String? = null
        private var paymentMethod: PaymentMethod? = null

        fun setShipping(shipping: Shipping?): Builder = apply {
            this.shipping = shipping
        }

        fun setPaymentIntent(paymentIntent: PaymentIntent?): Builder = apply {
            this.paymentIntent = paymentIntent
        }

        fun setToken(token: String?): Builder = apply {
            this.token = token
        }

        fun setPaymentMethod(paymentMethod: PaymentMethod?): Builder = apply {
            this.paymentMethod = paymentMethod
        }

        override fun build(): PaymentSessionConfiguration {
            return PaymentSessionConfiguration(
                shipping = shipping,
                paymentIntent = paymentIntent,
                token = token,
                paymentMethod = paymentMethod
            )
        }
    }
}