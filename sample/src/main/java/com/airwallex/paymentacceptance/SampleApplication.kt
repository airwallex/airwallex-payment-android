package com.airwallex.paymentacceptance

import android.app.Application
import com.airwallex.android.Airwallex
import com.airwallex.android.AirwallexConfiguration

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Airwallex.initialize(
            AirwallexConfiguration.Builder(this)
                .enableLogging(true)
                .build()
        )
    }
}