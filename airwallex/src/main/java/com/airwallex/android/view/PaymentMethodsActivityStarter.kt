package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.airwallex.android.model.AirwallexError
import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentMethodType
import com.airwallex.android.view.PaymentMethodsActivityStarter.Args
import kotlinx.android.parcel.Parcelize

internal class PaymentMethodsActivityStarter constructor(
    activity: Activity
) : ActivityStarter<PaymentMethodsActivity, Args>(
    activity,
    PaymentMethodsActivity::class.java,
    REQUEST_CODE
) {

    @Parcelize
    data class Args internal constructor(
        val paymentIntent: PaymentIntent,
        val token: String
    ) : ActivityStarter.Args {

        class Builder : ObjectBuilder<Args> {
            private lateinit var paymentIntent: PaymentIntent
            private lateinit var token: String

            fun setPaymentIntent(paymentIntent: PaymentIntent): Builder = apply {
                this.paymentIntent = paymentIntent
            }

            fun setToken(token: String): Builder = apply {
                this.token = token
            }

            override fun build(): Args {
                return Args(
                    paymentIntent = paymentIntent,
                    token = token
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
    data class Result internal constructor(
        val paymentIntent: PaymentIntent? = null,
        val paymentMethodType: PaymentMethodType? = null,
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
        const val REQUEST_CODE: Int = 1001
    }
}