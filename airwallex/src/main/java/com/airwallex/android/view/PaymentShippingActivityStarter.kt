package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.Shipping
import com.airwallex.android.view.PaymentShippingActivityStarter.Args
import kotlinx.android.parcel.Parcelize

internal class PaymentShippingActivityStarter constructor(
    activity: Activity
) : ActivityStarter<PaymentShippingActivity, Args>(
    activity,
    PaymentShippingActivity::class.java,
    REQUEST_CODE
) {

    @Parcelize
    data class Args internal constructor(
        internal val shipping: Shipping?
    ) : ActivityStarter.Args {

        class Builder : ObjectBuilder<Args> {
            private var shipping: Shipping? = null

            fun setShipping(shipping: Shipping?): Builder = apply {
                this.shipping = shipping
            }

            override fun build(): Args {
                return Args(
                    shipping = shipping
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
        val shipping: Shipping
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