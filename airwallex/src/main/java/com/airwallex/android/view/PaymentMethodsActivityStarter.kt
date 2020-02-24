package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.view.PaymentMethodsActivityStarter.PaymentMethodsArgs
import kotlinx.android.parcel.Parcelize

internal class PaymentMethodsActivityStarter constructor(
    activity: Activity
) : ActivityStarter<PaymentMethodsActivity, PaymentMethodsArgs>(
    activity,
    PaymentMethodsActivity::class.java,
    REQUEST_CODE
) {

    @Parcelize
    data class PaymentMethodsArgs internal constructor(
        val paymentMethod: PaymentMethod?,
        val clientSecret: String,
        val token: String,
        val shouldShowWechatPay: Boolean,
        val customerId: String
    ) : Args {

        class Builder(
            private val clientSecret: String,
            private val token: String,
            private val customerId: String
        ) :
            ObjectBuilder<PaymentMethodsArgs> {
            private var paymentMethod: PaymentMethod? = null
            private var shouldShowWechatPay: Boolean = false

            fun setPaymentMethod(paymentMethod: PaymentMethod?): Builder = apply {
                this.paymentMethod = paymentMethod
            }

            fun setShouldShowWechatPay(shouldShowWechatPay: Boolean): Builder =
                apply {
                    this.shouldShowWechatPay = shouldShowWechatPay
                }

            override fun build(): PaymentMethodsArgs {
                return PaymentMethodsArgs(
                    token = token,
                    paymentMethod = paymentMethod,
                    clientSecret = clientSecret,
                    shouldShowWechatPay = shouldShowWechatPay,
                    customerId = customerId
                )
            }
        }

        internal companion object {
            internal fun getExtra(intent: Intent): PaymentMethodsArgs {
                return requireNotNull(intent.getParcelableExtra(Args.AIRWALLEX_EXTRA))
            }
        }
    }

    @Parcelize
    data class Result internal constructor(
        val paymentMethod: PaymentMethod
    ) : ActivityStarter.Result {
        override fun toBundle(): Bundle {
            val bundle = Bundle()
            bundle.putParcelable(ActivityStarter.Result.AIRWALLEX_EXTRA, this)
            return bundle
        }

        companion object {
            fun fromIntent(intent: Intent?): Result? {
                return intent?.getParcelableExtra(ActivityStarter.Result.AIRWALLEX_EXTRA)
            }
        }
    }

    companion object {
        const val REQUEST_CODE: Int = 1001
    }
}