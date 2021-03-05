package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.support.v4.app.Fragment
import com.airwallex.android.model.ThreeDSecureLookup
import com.airwallex.android.view.ThreeDSecureActivityLaunch.Args
import kotlinx.android.parcel.Parcelize

internal class ThreeDSecureActivityLaunch : AirwallexActivityLaunch<ThreeDSecureActivity, Args> {

    constructor(activity: Activity) : super(
        activity,
        ThreeDSecureActivity::class.java,
        REQUEST_CODE
    )

    constructor(fragment: Fragment) : super(
        fragment,
        ThreeDSecureActivity::class.java,
        REQUEST_CODE
    )

    @Parcelize
    data class Args internal constructor(
        val threeDSecureLookup: ThreeDSecureLookup
    ) : AirwallexActivityLaunch.Args {

        internal companion object {
            internal fun getExtra(intent: Intent): Args {
                return requireNotNull(intent.getParcelableExtra(AirwallexActivityLaunch.Args.AIRWALLEX_EXTRA))
            }
        }
    }

    companion object {
        const val REQUEST_CODE: Int = 1006
    }
}
