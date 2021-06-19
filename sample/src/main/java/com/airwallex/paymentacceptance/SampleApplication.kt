package com.airwallex.paymentacceptance

import android.app.Application
import com.airwallex.android.Airwallex
import com.airwallex.android.AirwallexConfiguration
import com.airwallex.android.Environment

class SampleApplication : Application() {

    companion object {
        lateinit var instance: SampleApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        configAirwallex()
    }

    fun configAirwallex() {
        val environment = when (Settings.sdkEnv) {
            resources.getStringArray(R.array.array_sdk_env)[0] -> Environment.STAGING
            resources.getStringArray(R.array.array_sdk_env)[1] -> Environment.DEMO
            resources.getStringArray(R.array.array_sdk_env)[2] -> Environment.PRODUCTION
            else -> throw Exception("No environment")
        }

        Airwallex.initialize(
            AirwallexConfiguration.Builder()
                .enableLogging(true) // Enable log in sdk, best set to false in release version
                .setEnvironment(environment)
                .build()
        )
    }
}
