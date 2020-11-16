package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.airwallex.android.model.AirwallexError
import com.airwallex.android.model.Device
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.view.SelectCurrencyActivityLaunch.Args
import kotlinx.android.parcel.Parcelize

internal class SelectCurrencyActivityLaunch : AirwallexActivityLaunch<SelectCurrencyActivity, Args> {

    constructor(activity: Activity) : super(
        activity,
        SelectCurrencyActivity::class.java,
        REQUEST_CODE
    )

    constructor(fragment: Fragment) : super(
        fragment,
        SelectCurrencyActivity::class.java,
        REQUEST_CODE
    )

    @Parcelize
    internal data class Args internal constructor(
        val dcc: PaymentIntent.DccData,
        val paymentIntent: PaymentIntent,
        val clientSecret: String,
        val device: Device
    ) : AirwallexActivityLaunch.Args {

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
        const val REQUEST_CODE: Int = 1005
    }
}
