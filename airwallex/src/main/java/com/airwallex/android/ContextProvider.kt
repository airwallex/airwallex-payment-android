package com.airwallex.android

import android.content.Context

class ContextProvider {

    companion object {

        private lateinit var appContext: Context

        fun init(context: Context) {
            appContext = context.applicationContext
        }
    }
}