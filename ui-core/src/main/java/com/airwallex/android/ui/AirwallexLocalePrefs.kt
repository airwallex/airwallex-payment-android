package com.airwallex.android.ui

import android.content.Context
import androidx.annotation.RestrictTo
import java.util.Locale

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object AirwallexLocalePrefs {

    private const val PREFS_NAME = "airwallex-locale-prefs"
    private const val LOCALE_TAG = "airwallex-locale-tag"

    fun setLocaleTag(context: Context, localeTag: String?) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(LOCALE_TAG, localeTag)
            .apply()
    }

    fun getLocale(context: Context): Locale? {
        val localeTag = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(LOCALE_TAG, null)
            ?: return null
        return Locale.forLanguageTag(localeTag)
    }
}
