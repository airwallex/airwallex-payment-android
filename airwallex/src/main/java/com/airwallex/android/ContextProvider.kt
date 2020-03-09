package com.airwallex.android

import android.content.Context

internal class ContextProvider {

    companion object {

        /**
         * The Application Context that used on the SDK
         */
        internal lateinit var applicationContext: Context

        fun init(context: Context) {
            applicationContext = context.applicationContext
        }
    }
}
