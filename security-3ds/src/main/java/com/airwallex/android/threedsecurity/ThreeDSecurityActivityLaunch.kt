package com.airwallex.android.threedsecurity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import androidx.fragment.app.Fragment
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.model.Options
import com.airwallex.android.threedsecurity.ThreeDSecurityActivityLaunch.Args
import com.airwallex.android.ui.AirwallexActivityLaunch
import com.airwallex.android.ui.extension.getExtraResult
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

class ThreeDSecurityActivityLaunch : AirwallexActivityLaunch<ThreeDSecurityActivity, Args> {

    constructor(activity: Activity) : super(
        activity,
        ThreeDSecurityActivity::class.java,
        REQUEST_CODE
    )

    constructor(fragment: Fragment) : super(
        fragment,
        ThreeDSecurityActivity::class.java,
        REQUEST_CODE
    )

    @Parcelize
    data class Args internal constructor(
        val url: String,
        val body: String,
        val options: Options.ContinuePaymentIntentOptions
    ) : AirwallexActivityLaunch.Args

    @Parcelize
    data class Result internal constructor(
        val paymentIntentId: String? = null,
        val exception: AirwallexException? = null
    ) : AirwallexActivityLaunch.Result {
        override fun toBundle(): Bundle {
            return Bundle().also {
                it.putParcelable(AirwallexActivityLaunch.Result.AIRWALLEX_EXTRA, this)
            }
        }

        companion object : Parceler<Result> {
            override fun create(parcel: Parcel): Result {
                return Result(
                    paymentIntentId = parcel.readString(),
                    exception = parcel.readSerializable() as? AirwallexException
                )
            }

            override fun Result.write(parcel: Parcel, flags: Int) {
                parcel.writeString(paymentIntentId)
                parcel.writeSerializable(exception)
            }

            fun fromIntent(intent: Intent?): Result? {
                return intent?.getExtraResult()
            }
        }
    }

    companion object {
        const val REQUEST_CODE: Int = 1006
    }
}