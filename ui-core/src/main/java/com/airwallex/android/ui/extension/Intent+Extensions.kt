package com.airwallex.android.ui.extension

import android.content.Intent
import android.os.Build
import com.airwallex.android.ui.AirwallexActivityLaunch

inline fun <reified T> Intent.getExtraArgs(): T {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        requireNotNull(getParcelableExtra(AirwallexActivityLaunch.Args.AIRWALLEX_EXTRA, T::class.java))
    } else {
        requireNotNull(getParcelableExtra(AirwallexActivityLaunch.Args.AIRWALLEX_EXTRA))
    }
}

inline fun <reified T> Intent.getExtraResult(): T {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        requireNotNull(getParcelableExtra(AirwallexActivityLaunch.Result.AIRWALLEX_EXTRA, T::class.java))
    } else {
        requireNotNull(getParcelableExtra(AirwallexActivityLaunch.Result.AIRWALLEX_EXTRA))
    }
}
