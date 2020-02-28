package com.airwallex.android

import android.os.Parcelable
import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.Shipping
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentSessionData(
    val paymentIntent: PaymentIntent?,
    val token: String?,
    val shipping: Shipping?
) : Parcelable {

    class Builder : ObjectBuilder<PaymentSessionData> {

        private var paymentIntent: PaymentIntent? = null
        private var token: String? = null
        private var shipping: Shipping? = null

        fun setPaymentIntent(paymentIntent: PaymentIntent): Builder = apply {
            this.paymentIntent = paymentIntent
        }

        fun setToken(token: String): Builder = apply {
            this.token = token
        }

        fun setShipping(shipping: Shipping?): Builder = apply {
            this.shipping = shipping
        }

        override fun build(): PaymentSessionData {
            return PaymentSessionData(
                paymentIntent = paymentIntent,
                token = token,
                shipping = shipping
            )
        }
    }
}