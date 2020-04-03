package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.airwallex.android.model.*
import com.airwallex.android.view.PaymentCheckoutActivityStarter.Args
import kotlinx.android.parcel.Parcelize

internal class PaymentCheckoutActivityStarter constructor(
    activity: Activity
) : ActivityStarter<PaymentCheckoutActivity, Args>(
    activity,
    PaymentCheckoutActivity::class.java,
    REQUEST_CODE
) {

    @Parcelize
    internal data class Args internal constructor(
        val paymentIntent: PaymentIntent,
        val token: String,
        val paymentMethod: PaymentMethod,
        val cvc: String?
    ) : ActivityStarter.Args {

        class Builder : ObjectBuilder<Args> {

            private lateinit var paymentIntent: PaymentIntent
            private lateinit var token: String
            private lateinit var paymentMethod: PaymentMethod
            private var cvc: String? = null

            fun setCvc(cvc: String?): Builder = apply {
                this.cvc = cvc
            }

            fun setPaymentIntent(paymentIntent: PaymentIntent): Builder = apply {
                this.paymentIntent = paymentIntent
            }

            fun setToken(token: String): Builder = apply {
                this.token = token
            }

            fun setPaymentMethod(paymentMethod: PaymentMethod): Builder = apply {
                this.paymentMethod = paymentMethod
            }

            override fun build(): Args {
                return Args(
                    paymentIntent = paymentIntent,
                    token = token,
                    paymentMethod = paymentMethod,
                    cvc = cvc
                )
            }
        }

        internal companion object {
            internal fun getExtra(intent: Intent): Args {
                return requireNotNull(intent.getParcelableExtra(ActivityStarter.Args.AIRWALLEX_EXTRA))
            }
        }
    }

    @Parcelize
    internal data class Result internal constructor(
        val paymentIntent: PaymentIntent? = null,
        val error: AirwallexError? = null
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
        const val REQUEST_CODE: Int = 1004
    }
}
