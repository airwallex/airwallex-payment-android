package com.airwallex.android.core

import com.airwallex.android.core.log.AirwallexLogger
import java.util.IllformedLocaleException
import java.util.Locale

internal object LocaleValidator {

    fun validatedOrNull(locale: Locale?): Locale? {
        if (locale == null) return null

        return try {
            Locale.Builder().setLocale(locale).build()
        } catch (exception: IllformedLocaleException) {
            AirwallexLogger.error(
                "Invalid locale supplied; using host locale",
                exception
            )
            null
        }
    }
}
