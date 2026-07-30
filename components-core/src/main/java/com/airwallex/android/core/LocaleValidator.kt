package com.airwallex.android.core

import java.util.IllformedLocaleException
import java.util.Locale

internal object LocaleValidator {

    fun validate(locale: Locale?) {
        if (locale == null) return

        try {
            Locale.Builder().setLocale(locale).build()
        } catch (exception: IllformedLocaleException) {
            throw IllegalArgumentException("Invalid locale: $locale", exception)
        }
    }
}
