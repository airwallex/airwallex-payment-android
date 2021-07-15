package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import androidx.fragment.app.Fragment
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.NextAction
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.view.DccActivityLaunch.Args
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
        val exception: Exception? = null
    ) : AirwallexActivityLaunch.Result {
        override fun toBundle(): Bundle {
            return Bundle().also {
                it.putParcelable(AirwallexActivityLaunch.Result.AIRWALLEX_EXTRA, this)
            }
        }

        internal companion object : Parceler<Result> {
            override fun create(parcel: Parcel): Result {
                return Result(
                    paymentIntent = parcel.readParcelable(PaymentIntent::class.java.classLoader),
                    exception = parcel.readSerializable() as? AirwallexException?
                )
            }

            override fun Result.write(parcel: Parcel, flags: Int) {
                parcel.writeParcelable(paymentIntent, 0)
                parcel.writeSerializable(exception)
            }

            fun fromIntent(intent: Intent?): Result? {
                return intent?.getParcelableExtra(AirwallexActivityLaunch.Result.AIRWALLEX_EXTRA)
            }
        }
    }

    companion object {
        const val REQUEST_CODE: Int = 1005
    }
}
