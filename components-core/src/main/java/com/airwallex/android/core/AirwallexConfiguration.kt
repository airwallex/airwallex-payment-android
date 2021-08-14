package com.airwallex.android.core

data class AirwallexConfiguration internal constructor(
    val enableLogging: Boolean,
    val environment: Environment,
    val supportComponentProviders: List<ActionComponentProvider<out ActionComponent>>
) {
    class Builder {

        /**
         * You can set to true if you want to see more debug logs
         */
        private var enableLogging: Boolean = false

        /**
         * Set the environment to be used by Airwallex
         */
        private var environment: Environment = Environment.PRODUCTION

        /**
         * Supported ComponentProvider (Card, WeChat, Redirect)
         */
        private var supportComponentProviders: List<ActionComponentProvider<out ActionComponent>> =
            listOf()

        fun enableLogging(enable: Boolean): Builder = apply {
            this.enableLogging = enable
        }

        fun setEnvironment(environment: Environment): Builder = apply {
            this.environment = environment
        }

        fun setSupportComponentProviders(supportComponentProviders: List<ActionComponentProvider<out ActionComponent>>): Builder =
            apply {
                this.supportComponentProviders = supportComponentProviders
            }

        fun build(): AirwallexConfiguration {
            return AirwallexConfiguration(
                enableLogging = enableLogging,
                environment = environment,
                supportComponentProviders = supportComponentProviders
            )
        }
    }
}
