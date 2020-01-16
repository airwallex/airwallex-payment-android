package com.airwallex.example

import android.app.Application
import com.airwallex.android.Airwallex
import com.airwallex.android.AirwallexConfiguration
import com.airwallex.android.Environment

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Airwallex.initialize(
            AirwallexConfiguration.Builder(this)
                .setEnvironment(Environment.STAGING)
                .enableLogging(true)
                .build()
        )
    }
}