package com.airwallex.android

object AirwallexPlugins {

    lateinit var configuration: AirwallexConfiguration

    fun initialize(configuration: AirwallexConfiguration) {
        this.configuration = configuration
    }

    fun baseUrl(): String {
        return configuration.environment.baseUrl
    }

    fun authUrl(): String {
        return configuration.environment.authUrl
    }
}