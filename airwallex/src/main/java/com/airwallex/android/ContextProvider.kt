package com.airwallex.android

import android.content.Context

internal class ContextProvider {

    companion object {

        private lateinit var appContext: Context

        fun init(context: Context) {
            appContext = context.applicationContext
        }
    }
}