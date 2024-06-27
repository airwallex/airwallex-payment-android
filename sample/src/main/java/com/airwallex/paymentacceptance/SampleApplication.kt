package com.airwallex.paymentacceptance

import android.app.Application
import com.airwallex.android.AirwallexStarter
import com.airwallex.android.card.CardComponent
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexConfiguration
import com.airwallex.android.core.Environment
import com.airwallex.android.googlepay.GooglePayComponent
import com.airwallex.android.redirect.RedirectComponent
import com.airwallex.android.wechat.WeChatComponent

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
        AirwallexStarter.init(this)
        Airwallex.initialize(
            AirwallexConfiguration.Builder()
                .enableLogging(true) // Enable log in sdk, best set to false in release version
                .setEnvironment(environment)
                .setSupportComponentProviders(
                    listOf(
                        CardComponent.PROVIDER,
                        WeChatComponent.PROVIDER,
                        RedirectComponent.PROVIDER,
                        GooglePayComponent.PROVIDER
                    )
                )
                .build(),
            ExampleClientSecretProvider()
        )
    }
}
