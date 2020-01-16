package com.airwallex.android

object AirwallexPlugins {

    private lateinit var configuration: AirwallexConfiguration

    fun initialize(configuration: AirwallexConfiguration) {
        this.configuration = configuration
    }

    val baseUrl by lazy { configuration.environment.baseUrl }
    val authUrl by lazy { configuration.environment.authUrl }

    val enableLogging by lazy { configuration.enableLogging }

}