package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.airwallex.android.CustomerSessionConfig
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.*
import com.airwallex.android.model.ObjectBuilder
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
    data class Args internal constructor(
        val customerSessionConfig: CustomerSessionConfig,
        val paymentMethod: PaymentMethod,
        val cvc: String?
    ) : ActivityStarter.Args {

        class Builder(
            private val customerSessionConfig: CustomerSessionConfig,
            private val paymentMethod: PaymentMethod
        ) :
            ObjectBuilder<Args> {

            private var cvc: String? = null

            internal fun setCvc(cvc: String?): Builder = apply {
                this.cvc = cvc
            }

            override fun build(): Args {
                return Args(
                    customerSessionConfig = customerSessionConfig,
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
        const val REQUEST_CODE: Int = 1004
    }

}