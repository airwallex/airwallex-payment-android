package com.airwallex.android.ui.extension

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.annotation.RestrictTo
import java.util.Locale

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun Context.localizedForAirwallex(locale: Locale?): Context {
    if (locale == null) return this

    val localizedConfiguration = Configuration(resources.configuration).apply {
        setLocale(locale)
        setLayoutDirection(locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setLocales(LocaleList(locale))
        }
    }
    return createConfigurationContext(localizedConfiguration)
}

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun Context.airwallexLocale(): Locale {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        resources.configuration.locales[0]
    } else {
        @Suppress("DEPRECATION")
        resources.configuration.locale
    }
}
