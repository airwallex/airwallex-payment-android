package com.airwallex.android.card.view.dcc

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import androidx.fragment.app.Fragment
import com.airwallex.android.card.view.dcc.DccActivityLaunch.Args
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.model.NextAction
import com.airwallex.android.ui.AirwallexActivityLaunch
import com.airwallex.android.ui.extension.getExtraResult
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

internal class DccActivityLaunch : AirwallexActivityLaunch<DccActivity, Args> {

    constructor(activity: Activity) : super(
        activity,
        DccActivity::class.java,
        REQUEST_CODE
    )

    constructor(fragment: Fragment) : super(
        fragment,
        DccActivity::class.java,
        REQUEST_CODE
    )

    @Parcelize
    internal data class Args internal constructor(
        val dcc: NextAction.DccData,
        val paymentIntentId: String,
        val currency: String,
        val amount: BigDecimal,
        val clientSecret: String,
    ) : AirwallexActivityLaunch.Args

    @Parcelize
    internal data class Result internal constructor(
        val paymentIntentId: String? = null,
        val exception: AirwallexException? = null
    ) : AirwallexActivityLaunch.Result {
        override fun toBundle(): Bundle {
            return Bundle().also {
                it.putParcelable(AirwallexActivityLaunch.Result.AIRWALLEX_EXTRA, this)
            }
        }

        internal companion object : Parceler<Result> {
            override fun create(parcel: Parcel): Result {
                return Result(
                    paymentIntentId = parcel.readString(),
                    exception = parcel.readSerializable() as? AirwallexException?
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
        const val REQUEST_CODE: Int = 1005
    }
}
