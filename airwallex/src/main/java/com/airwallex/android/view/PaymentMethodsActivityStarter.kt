package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.view.PaymentMethodsActivityStarter.PaymentMethodsArgs
import kotlinx.android.parcel.Parcelize

class PaymentMethodsActivityStarter constructor(
    activity: Activity
) : ActivityStarter<PaymentMethodsActivity, PaymentMethodsArgs>(
    activity,
    PaymentMethodsActivity::class.java,
    REQUEST_CODE
) {

    @Parcelize
    data class PaymentMethodsArgs internal constructor(
        val paymentMethod: PaymentMethod?,
        val paymentIntent: PaymentIntent,
        val token: String,
        private val shouldShowWechatPay: Boolean
    ) : Args {

        class Builder(
            private val paymentIntent: PaymentIntent,
            private val token: String

        ) :
            ObjectBuilder<PaymentMethodsArgs> {
            private var paymentMethod: PaymentMethod? = null
            private var shouldShowWechatPay: Boolean = false

            fun setPaymentMethod(paymentMethod: PaymentMethod?): Builder = apply {
                this.paymentMethod = paymentMethod
            }

            fun setShouldShowWechatPay(shouldShowWechatPay: Boolean): Builder = apply {
                this.shouldShowWechatPay = shouldShowWechatPay
            }

            override fun build(): PaymentMethodsArgs {
                return PaymentMethodsArgs(
                    token = token,
                    paymentMethod = paymentMethod,
                    paymentIntent = paymentIntent,
                    shouldShowWechatPay = shouldShowWechatPay
                )
            }
        }

        internal companion object {
            internal fun create(intent: Intent): PaymentMethodsArgs {
                return requireNotNull(intent.getParcelableExtra(Args.AIRWALLEX_EXTRA))
            }
        }
    }

    @Parcelize
    data class Result internal constructor(
        val paymentMethod: PaymentMethod,
        val cvc: String?
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