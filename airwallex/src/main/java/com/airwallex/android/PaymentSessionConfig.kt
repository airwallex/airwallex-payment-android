package com.airwallex.android

import android.os.Parcelable
import com.airwallex.android.model.ObjectBuilder
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentSessionConfig internal constructor(
    val shouldShowWechatPay: Boolean
) : Parcelable {

    class Builder : ObjectBuilder<PaymentSessionConfig> {
        private var shouldShowWechatPay: Boolean = false

        fun setShouldShowWechatPay(shouldShowWechatPay: Boolean): Builder = apply {
            this.shouldShowWechatPay = shouldShowWechatPay
        }

        override fun build(): PaymentSessionConfig {
            return PaymentSessionConfig(
                shouldShowWechatPay = shouldShowWechatPay
            )
        }
    }
}