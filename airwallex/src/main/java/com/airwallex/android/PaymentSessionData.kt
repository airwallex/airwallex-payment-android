package com.airwallex.android

import android.os.Parcelable
import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.Shipping
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentSessionData(
    val clientSecret: String?,
    val token: String?,
    val customerId: String?,
    val paymentMethod: PaymentMethod?,
    val billing: PaymentMethod.Billing?,
    val shipping: Shipping?,
    val shouldShowWechatPay: Boolean = false
) : Parcelable {

    class Builder : ObjectBuilder<PaymentSessionData> {

        private var clientSecret: String? = null
        private var token: String? = null
        private var customerId: String? = null
        private var paymentMethod: PaymentMethod? = null
        private var shipping: Shipping? = null
        private var billing: PaymentMethod.Billing? = null
        private var shouldShowWechatPay: Boolean = false

        fun setClientSecret(clientSecret: String): Builder = apply {
            this.clientSecret = clientSecret
        }

        fun setToken(token: String): Builder = apply {
            this.token = token
        }

        fun setCustomerId(customerId: String): Builder = apply {
            this.customerId = customerId
        }

        fun setPaymentMethod(paymentMethod: PaymentMethod?): Builder = apply {
            this.paymentMethod = paymentMethod
        }

        fun setShipping(shipping: Shipping?): Builder = apply {
            this.shipping = shipping
        }

        fun setBilling(billing: PaymentMethod.Billing?): Builder = apply {
            this.billing = billing
        }

        fun setShouldShowWechatPay(shouldShowWechatPay: Boolean): Builder = apply {
            this.shouldShowWechatPay = shouldShowWechatPay
        }

        override fun build(): PaymentSessionData {
            return PaymentSessionData(
                clientSecret = clientSecret,
                token = token,
                customerId = customerId,
                paymentMethod = paymentMethod,
                shipping = shipping,
                billing = billing,
                shouldShowWechatPay = shouldShowWechatPay
            )
        }
    }
}