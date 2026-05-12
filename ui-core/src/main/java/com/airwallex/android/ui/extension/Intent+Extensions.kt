package com.airwallex.android.ui.extension

import android.content.Intent
import android.os.Build
import com.airwallex.android.ui.AirwallexActivityLaunch

inline fun <reified T> Intent.getExtraArgs(): T = requireNotNull(getExtraArgsOrNull())

inline fun <reified T> Intent.getExtraArgsOrNull(): T? {
    val bundle = getBundleExtra(AirwallexActivityLaunch.Args.AIRWALLEX_BUNDLE_EXTRA) ?: return null
    // After process death the framework re-marshals the launch Intent without
    // attaching the app classloader to nested bundles, so getParcelable silently
    // returns null. Set it explicitly before reading.
    bundle.classLoader = T::class.java.classLoader
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        bundle.getParcelable(AirwallexActivityLaunch.Args.AIRWALLEX_EXTRA, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        bundle.getParcelable(AirwallexActivityLaunch.Args.AIRWALLEX_EXTRA)
    }
}

inline fun <reified T> Intent.getExtraResult(): T = requireNotNull(getExtraResultOrNull())

inline fun <reified T> Intent.getExtraResultOrNull(): T? {
    setExtrasClassLoader(T::class.java.classLoader)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(AirwallexActivityLaunch.Result.AIRWALLEX_EXTRA, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getParcelableExtra(AirwallexActivityLaunch.Result.AIRWALLEX_EXTRA)
    }
}
