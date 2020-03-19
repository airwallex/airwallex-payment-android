package com.airwallex.paymentacceptance

import android.content.Context

class ContextProvider {
    companion object {

        lateinit var applicationContext: Context

        fun init(context: Context) {
            applicationContext = context.applicationContext
        }
    }
}