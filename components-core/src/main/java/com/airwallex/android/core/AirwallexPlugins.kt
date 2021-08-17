package com.airwallex.android.core

import android.content.Context
import com.airwallex.android.core.model.PaymentMethodType

/**
 * Provide some internal plugins
 */
object AirwallexPlugins {

    const val AIRWALLEX_USER_AGENT = "Airwallex-Android-SDK"

    private var configuration: AirwallexConfiguration =
        AirwallexConfiguration(false, Environment.PRODUCTION, emptyList())

    fun getSdkVersion(context: Context): String {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return packageInfo.versionName.toString()
    }

    fun initialize(configuration: AirwallexConfiguration) {
        this.configuration = configuration
    }

    /**
     * Enable logging in the Airwallex
     */
    internal val enableLogging: Boolean
        get() {
            return configuration.enableLogging
        }

    /**
     * Environment in the Airwallex
     */
    val environment: Environment
        get() {
            return configuration.environment
        }

    fun getProvider(paymentMethodType: PaymentMethodType): ActionComponentProvider<out ActionComponent>? {
        return configuration.supportComponentProviders
            .firstOrNull {
                it.canHandleAction(
                    paymentMethodType
                )
            }
    }
}
