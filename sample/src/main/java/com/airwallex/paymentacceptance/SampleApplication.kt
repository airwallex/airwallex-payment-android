package com.airwallex.paymentacceptance

import android.app.Application

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ContextProvider.init(this)
    }
}
