package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.view.PaymentMethodsActivityStarter.Args
import kotlinx.android.parcel.Parcelize

class PaymentMethodsActivityStarter constructor(
    activity: Activity
) : ActivityStarter<PaymentMethodsActivity, Args>(
    activity,
    PaymentMethodsActivity::class.java,
    Args.DEFAULT,
    REQUEST_CODE
) {

    @Parcelize
    data class Args internal constructor(
        val paymentMethod: PaymentMethod?,
        val paymentIntent: PaymentIntent?,
        val token: String?
    ) : ActivityStarter.Args {

        class Builder : ObjectBuilder<Args> {
            private var paymentMethod: PaymentMethod? = null
            private var paymentIntent: PaymentIntent? = null
            private var token: String? = null

            fun setPaymentMethod(paymentMethod: PaymentMethod?): Builder = apply {
                this.paymentMethod = paymentMethod
            }

            fun setPaymentIntent(paymentIntent: PaymentIntent?): Builder = apply {
                this.paymentIntent = paymentIntent
            }

            fun setToken(token: String?): Builder = apply {
                this.token = token
            }

            override fun build(): Args {
                return Args(
                    token = token,
                    paymentMethod = paymentMethod,
                    paymentIntent = paymentIntent
                )
            }
        }

        internal companion object {
            internal val DEFAULT = Builder().build()

            @JvmSynthetic
            internal fun create(intent: Intent): Args {
                return requireNotNull(intent.getParcelableExtra(ActivityStarter.Args.EXTRA))
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
            bundle.putParcelable(ActivityStarter.Result.EXTRA, this)
            return bundle
        }

        companion object {
            fun fromIntent(intent: Intent?): Result? {
                return intent?.getParcelableExtra(ActivityStarter.Result.EXTRA)
            }
        }
    }

    companion object {
        const val REQUEST_CODE: Int = 1003
    }

}