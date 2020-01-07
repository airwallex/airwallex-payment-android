package com.airwallex.android.util

import android.content.Context

class ContextProvider {

    companion object {

        lateinit var appContext: Context

        fun init(context: Context) {
            appContext = context.applicationContext
        }
    }
}