package com.airwallex.android.core

import android.content.Context

/**
 * Provide some internal plugins
 */
object AirwallexPlugins {

    const val AIRWALLEX_USER_AGENT = "Airwallex-Android-SDK"

    private lateinit var configuration: AirwallexConfiguration

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
}
