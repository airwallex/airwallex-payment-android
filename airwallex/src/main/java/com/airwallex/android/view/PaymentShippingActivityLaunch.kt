package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.Shipping
import com.airwallex.android.view.PaymentShippingActivityLaunch.Args
import kotlinx.android.parcel.Parcelize

class PaymentShippingActivityLaunch : AirwallexActivityLaunch<PaymentShippingActivity, Args> {

    constructor(activity: Activity) : super(
        activity,
        PaymentShippingActivity::class.java,
        REQUEST_CODE
    )

    constructor(fragment: Fragment) : super(
        fragment,
        PaymentShippingActivity::class.java,
        REQUEST_CODE
    )

    @Parcelize
    data class Args internal constructor(
        internal val shipping: Shipping?
    ) : AirwallexActivityLaunch.Args {

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
                return requireNotNull(intent.getParcelableExtra(AirwallexActivityLaunch.Args.AIRWALLEX_EXTRA))
            }
        }
    }

    @Parcelize
    internal data class Result internal constructor(
        val shipping: Shipping
    ) : AirwallexActivityLaunch.Result {
        override fun toBundle(): Bundle {
            return Bundle().also {
                it.putParcelable(AirwallexActivityLaunch.Result.AIRWALLEX_EXTRA, this)
            }
        }

        companion object {
            fun fromIntent(intent: Intent?): Result? {
                return intent?.getParcelableExtra(AirwallexActivityLaunch.Result.AIRWALLEX_EXTRA)
            }
        }
    }

    companion object {
        const val REQUEST_CODE: Int = 1003
    }
}
