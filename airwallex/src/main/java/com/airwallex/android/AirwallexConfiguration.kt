package com.airwallex.android

import android.content.Context

data class AirwallexConfiguration constructor(val builder: Builder) {

    val environment: Environment = builder.environment
    val enableLogging: Boolean = builder.enableLogging

    class Builder(val context: Context) {

        init {
            ContextProvider.init(context)
        }

        var environment: Environment = Environment.PRODUCTION
        var enableLogging: Boolean = false

        fun setEnvironment(environment: Environment): Builder = apply {
            this.environment = environment
        }

        fun enableLogging(enable: Boolean): Builder = apply {
            this.enableLogging = enable
        }

        fun build(): AirwallexConfiguration {
            return AirwallexConfiguration(this)
        }
    }
}