package com.airwallex.android

import android.content.Context

/**
 * Provide some internal plugins
 */
internal object AirwallexPlugins {

    private lateinit var configuration: AirwallexConfiguration

    fun getSdkVersion(context: Context): String {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return packageInfo.versionName.toString()
    }

    internal fun initialize(configuration: AirwallexConfiguration) {
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
    internal val environment: Environment
        get() {
            return configuration.environment
        }
}
