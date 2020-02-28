package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentMethod
import kotlinx.android.parcel.Parcelize

internal class PaymentCheckoutActivityStarter constructor(
    activity: Activity
) : ActivityStarter<PaymentCheckoutActivity, PaymentCheckoutActivityStarter.Args>(
    activity,
    PaymentCheckoutActivity::class.java,
    REQUEST_CODE
) {

    @Parcelize
    data class Args internal constructor(
        val paymentIntent: PaymentIntent?,
        val token: String?,
        val paymentMethod: PaymentMethod?
    ) : ActivityStarter.Args {

        class Builder :
            ObjectBuilder<Args> {
            private var paymentIntent: PaymentIntent? = null
            private var token: String? = null
            private var paymentMethod: PaymentMethod? = null

            fun setPaymentIntent(paymentIntent: PaymentIntent?): Builder = apply {
                this.paymentIntent = paymentIntent
            }

            fun setToken(token: String?): Builder = apply {
                this.token = token
            }

            fun setPaymentMethod(paymentMethod: PaymentMethod?): Builder = apply {
                this.paymentMethod = paymentMethod
            }

            override fun build(): Args {
                return Args(
                    token = token,
                    paymentIntent = paymentIntent,
                    paymentMethod = paymentMethod
                )
            }
        }

        internal companion object {
            internal fun getExtra(intent: Intent): Args {
                return requireNotNull(intent.getParcelableExtra(ActivityStarter.Args.AIRWALLEX_EXTRA))
            }
        }
    }

    companion object {
        const val REQUEST_CODE: Int = 1002
    }

}