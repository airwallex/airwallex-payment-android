package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.airwallex.android.model.*
import com.airwallex.android.view.PaymentMethodsActivityLaunch.Args
import kotlinx.android.parcel.Parcelize

internal class PaymentMethodsActivityLaunch constructor(
    activity: Activity
) : AirwallexActivityLaunch<PaymentMethodsActivity, Args>(
    activity,
    PaymentMethodsActivity::class.java,
    REQUEST_CODE
) {

    @Parcelize
    internal data class Args internal constructor(
        val paymentIntent: PaymentIntent,
        val includeCheckoutFlow: Boolean
    ) : AirwallexActivityLaunch.Args {

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
                return requireNotNull(intent.getParcelableExtra(AirwallexActivityLaunch.Args.AIRWALLEX_EXTRA))
            }
        }
    }

    @Parcelize
    internal data class Result internal constructor(
        val paymentIntent: PaymentIntent? = null,
        val paymentMethodType: PaymentMethodType? = null,
        val error: AirwallexError? = null,
        val paymentMethod: PaymentMethod? = null,
        val cvc: String? = null,
        val includeCheckoutFlow: Boolean = false
    ) : AirwallexActivityLaunch.Result {
        override fun toBundle(): Bundle {
            val bundle = Bundle()
            bundle.putParcelable(AirwallexActivityLaunch.Result.AIRWALLEX_EXTRA, this)
            return bundle
        }

        companion object {
            fun fromIntent(intent: Intent?): Result? {
                return intent?.getParcelableExtra(AirwallexActivityLaunch.Result.AIRWALLEX_EXTRA)
            }
        }
    }

    companion object {
        const val REQUEST_CODE: Int = 1001
    }
}
