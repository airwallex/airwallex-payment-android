package com.airwallex.android

import android.content.Context

data class AirwallexConfiguration(
    val environment: Environment,
    val enableLogging: Boolean
) {

    class Builder(val context: Context) {

        init {
            ContextProvider.init(context)
        }

        private var environment: Environment = Environment.PRODUCTION
        private var enableLogging: Boolean = false

        fun setEnvironment(environment: Environment): Builder = apply {
            this.environment = environment
        }

        fun enableLogging(enable: Boolean): Builder = apply {
            this.enableLogging = enable
        }

        fun build(): AirwallexConfiguration {
            return AirwallexConfiguration(
                environment,
                enableLogging
            )
        }
    }
}