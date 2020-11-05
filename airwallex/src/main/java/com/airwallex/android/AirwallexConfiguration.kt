package com.airwallex.android

data class AirwallexConfiguration internal constructor(
    val enableLogging: Boolean,
    val environment: Environment
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


        fun enableLogging(enable: Boolean): Builder = apply {
            this.enableLogging = enable
        }

        fun setEnvironment(environment: Environment): Builder = apply {
            this.environment = environment
        }

        fun build(): AirwallexConfiguration {
            return AirwallexConfiguration(
                enableLogging = enableLogging,
                environment = environment
            )
        }
    }
}
