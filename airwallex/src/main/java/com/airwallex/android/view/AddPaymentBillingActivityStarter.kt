package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.view.AddPaymentBillingActivityStarter.BillingArgs
import kotlinx.android.parcel.Parcelize

class AddPaymentBillingActivityStarter constructor(
    activity: Activity
) : ActivityStarter<AddPaymentBillingActivity, BillingArgs>(
    activity,
    AddPaymentBillingActivity::class.java,
    REQUEST_CODE
) {

    @Parcelize
    data class BillingArgs internal constructor(
        internal val billing: PaymentMethod.Billing?
    ) : Args {

        class Builder : ObjectBuilder<BillingArgs> {
            private var billing: PaymentMethod.Billing? = null

            fun setBilling(billing: PaymentMethod.Billing?): Builder = apply {
                this.billing = billing
            }

            override fun build(): BillingArgs {
                return BillingArgs(
                    billing = billing
                )
            }
        }

        internal companion object {
            internal fun create(intent: Intent): BillingArgs {
                return requireNotNull(intent.getParcelableExtra(Args.AIRWALLEX_EXTRA))
            }
        }
    }

    @Parcelize
    data class Result internal constructor(
        val billing: PaymentMethod.Billing?
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