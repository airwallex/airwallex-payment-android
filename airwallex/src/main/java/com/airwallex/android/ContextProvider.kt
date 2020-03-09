package com.airwallex.android

import android.content.Context

internal class ContextProvider {

    companion object {

        internal lateinit var applicationContext: Context

        fun init(context: Context) {
            applicationContext = context.applicationContext
        }
    }
}
