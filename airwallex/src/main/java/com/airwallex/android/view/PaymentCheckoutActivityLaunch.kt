package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.airwallex.android.model.AirwallexError
import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.view.PaymentCheckoutActivityLaunch.Args
import kotlinx.android.parcel.Parcelize

class PaymentCheckoutActivityLaunch : AirwallexActivityLaunch<PaymentCheckoutActivity, Args> {

    constructor(activity: Activity) : super(
        activity,
        PaymentCheckoutActivity::class.java,
        REQUEST_CODE
    )

    constructor(fragment: Fragment) : super(
        fragment,
        PaymentCheckoutActivity::class.java,
        REQUEST_CODE
    )

    @Parcelize
    data class Args internal constructor(
        val paymentIntent: PaymentIntent,
        val paymentMethod: PaymentMethod,
        val cvc: String?
    ) : AirwallexActivityLaunch.Args {

        class Builder : ObjectBuilder<Args> {

            private lateinit var paymentIntent: PaymentIntent
            private lateinit var paymentMethod: PaymentMethod
            private var cvc: String? = null

            fun setCvc(cvc: String?): Builder = apply {
                this.cvc = cvc
            }

            fun setPaymentIntent(paymentIntent: PaymentIntent): Builder = apply {
                this.paymentIntent = paymentIntent
            }

            fun setPaymentMethod(paymentMethod: PaymentMethod): Builder = apply {
                this.paymentMethod = paymentMethod
            }

            override fun build(): Args {
                return Args(
                    paymentIntent = paymentIntent,
                    paymentMethod = paymentMethod,
                    cvc = cvc
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
        val error: AirwallexError? = null
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
        const val REQUEST_CODE: Int = 1004
    }
}
