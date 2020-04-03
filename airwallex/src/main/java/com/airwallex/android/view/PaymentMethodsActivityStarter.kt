package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.airwallex.android.model.*
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
    internal data class Args internal constructor(
        val paymentIntent: PaymentIntent,
        val includeCheckoutFlow: Boolean
    ) : ActivityStarter.Args {

        class Builder : ObjectBuilder<Args> {
            private lateinit var paymentIntent: PaymentIntent
            private var includeCheckoutFlow: Boolean = true

            fun setPaymentIntent(paymentIntent: PaymentIntent): Builder = apply {
                this.paymentIntent = paymentIntent
            }

            fun setIncludeCheckoutFlow(includeCheckoutFlow: Boolean): Builder = apply {
                this.includeCheckoutFlow = includeCheckoutFlow
            }

            override fun build(): Args {
                return Args(
                    paymentIntent = paymentIntent,
                    includeCheckoutFlow = includeCheckoutFlow
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
        val paymentMethodType: PaymentMethodType? = null,
        val error: AirwallexError? = null,
        val paymentMethod: PaymentMethod? = null,
        val cvc: String? = null
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
