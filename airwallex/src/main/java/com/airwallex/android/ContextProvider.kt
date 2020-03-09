package com.airwallex.android

import android.content.Context

internal class ContextProvider {

    companion object {

        lateinit var applicationContext: Context

        fun init(context: Context) {
            applicationContext = context.applicationContext
        }
    }
}
