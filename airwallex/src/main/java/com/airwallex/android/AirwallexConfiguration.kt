package com.airwallex.android

data class AirwallexConfiguration internal constructor(
    val enableLogging: Boolean,
    val baseUrl: String
) {
    class Builder {

        /**
         * You can set to true if you want to see more debug logs
         */
        private var enableLogging: Boolean = false

        /**
         * You can set it to different urls and test on different environments
         */
        private var baseUrl: String = Airwallex.BASE_URL

        fun enableLogging(enable: Boolean): Builder = apply {
            this.enableLogging = enable
        }

        fun setBaseUrl(baseUrl: String): Builder = apply {
            this.baseUrl = baseUrl
        }

        fun build(): AirwallexConfiguration {
            return AirwallexConfiguration(
                enableLogging = enableLogging,
                baseUrl = baseUrl
            )
        }
    }
}
