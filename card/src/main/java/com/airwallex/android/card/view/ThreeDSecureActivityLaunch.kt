package com.airwallex.android.card.view

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.airwallex.android.card.view.ThreeDSecureActivityLaunch.Args
import com.airwallex.android.core.model.ThreeDSecureLookup
import com.airwallex.android.ui.view.AirwallexActivityLaunch
import kotlinx.parcelize.Parcelize

class ThreeDSecureActivityLaunch : AirwallexActivityLaunch<ThreeDSecureActivity, Args> {

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
    data class Args(
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
