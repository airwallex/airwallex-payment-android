package com.airwallex.android

import android.os.Parcelable
import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.Shipping
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentSessionConfig(
    val shipping: Shipping?
) : Parcelable {

    class Builder : ObjectBuilder<PaymentSessionConfig> {

        private var shipping: Shipping? = null

        fun setShipping(shipping: Shipping?): Builder = apply {
            this.shipping = shipping
        }

        override fun build(): PaymentSessionConfig {
            return PaymentSessionConfig(
                shipping = shipping
            )
        }
    }
}