package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.view.AddPaymentBillingActivityStarter.Args
import kotlinx.android.parcel.Parcelize

class AddPaymentBillingActivityStarter constructor(
    activity: Activity
) : ActivityStarter<AddPaymentBillingActivity, Args>(
    activity,
    AddPaymentBillingActivity::class.java,
    Args.DEFAULT,
    REQUEST_CODE
) {

    @Parcelize
    data class Args internal constructor(
        internal val sameAsShipping: Boolean,
        internal val billing: PaymentMethod.Billing?
    ) : ActivityStarter.Args {

        class Builder : ObjectBuilder<Args> {
            private var sameAsShipping: Boolean = true
            private var billing: PaymentMethod.Billing? = null

            fun setSameAsShipping(sameAsShipping: Boolean): Builder = apply {
                this.sameAsShipping = sameAsShipping
            }

            fun setBilling(billing: PaymentMethod.Billing?): Builder = apply {
                this.billing = billing
            }

            override fun build(): Args {
                return Args(
                    sameAsShipping = sameAsShipping,
                    billing = billing
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
        val billing: PaymentMethod.Billing?,
        val sameAsShipping: Boolean
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
        const val REQUEST_CODE: Int = 1002
    }

}