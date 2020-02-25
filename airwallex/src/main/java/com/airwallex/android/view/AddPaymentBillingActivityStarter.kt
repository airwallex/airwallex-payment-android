package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.airwallex.android.model.Billing
import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.view.AddPaymentBillingActivityStarter.Args
import kotlinx.android.parcel.Parcelize

internal class AddPaymentBillingActivityStarter constructor(
    activity: Activity
) : ActivityStarter<AddPaymentBillingActivity, Args>(
    activity,
    AddPaymentBillingActivity::class.java,
    REQUEST_CODE
) {

    @Parcelize
    data class Args internal constructor(
        internal val billing: Billing?
    ) : ActivityStarter.Args {

        class Builder : ObjectBuilder<Args> {
            private var billing: Billing? = null

            fun setBilling(billing: Billing?): Builder = apply {
                this.billing = billing
            }

            override fun build(): Args {
                return Args(
                    billing = billing
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
        val billing: Billing?
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
        const val REQUEST_CODE: Int = 1003
    }

}