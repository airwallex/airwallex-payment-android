package com.airwallex.android.ui.extension

import android.content.Intent
import android.os.Build
import com.airwallex.android.ui.AirwallexActivityLaunch

inline fun <reified T> Intent.getExtraArgs(): T {
    return getBundleExtra(AirwallexActivityLaunch.Args.AIRWALLEX_BUNDLE_EXTRA).let {
        requireNotNull(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it?.getParcelable(AirwallexActivityLaunch.Args.AIRWALLEX_EXTRA, T::class.java)
            } else {
                it?.getParcelable(AirwallexActivityLaunch.Args.AIRWALLEX_EXTRA)
            }
        )
    }
}

inline fun <reified T> Intent.getExtraResult(): T {
    return requireNotNull(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableExtra(AirwallexActivityLaunch.Result.AIRWALLEX_EXTRA, T::class.java)
        } else {
            getParcelableExtra(AirwallexActivityLaunch.Result.AIRWALLEX_EXTRA)
        }
    )
}