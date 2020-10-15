package com.airwallex.paymentacceptance

import android.app.Application
import com.airwallex.android.Airwallex
import com.airwallex.android.AirwallexConfiguration
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalEnvironment

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
        Airwallex.initialize(
            AirwallexConfiguration.Builder()
                .enableLogging(true) // Enable log in sdk, best set to false in release version
                .setBaseUrl(Settings.baseUrl) // You can change the baseUrl to test other environments
                .setThreeDSecureEnv(if (Settings.threeDSecureEnv == "STAGING") CardinalEnvironment.STAGING else CardinalEnvironment.PRODUCTION)
                .build()
        )
    }
}
