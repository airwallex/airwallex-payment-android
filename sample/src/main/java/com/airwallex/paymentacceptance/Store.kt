package com.airwallex.paymentacceptance

import android.content.SharedPreferences

object Store {

    private val NAME = Store::class.java.canonicalName

    private const val TOKEN = "token"

    private val prefs: SharedPreferences =
        SampleApplication.instance.getSharedPreferences(NAME, 0)

    var token: String
        set(value) {
            prefs.edit()
                .putString(TOKEN, value)
                .apply()
        }
        get() {
            return prefs.getString(TOKEN, "") ?: ""
        }
}