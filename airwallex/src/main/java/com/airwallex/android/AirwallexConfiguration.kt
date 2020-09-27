package com.airwallex.android

import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalEnvironment

data class AirwallexConfiguration internal constructor(
    val enableLogging: Boolean,
    val baseUrl: String,
    val threeDSecureEnv: CardinalEnvironment
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

        /**
         * You can set it to STAGING to test
         */
        private var threeDSecureEnv: CardinalEnvironment = CardinalEnvironment.PRODUCTION

        fun enableLogging(enable: Boolean): Builder = apply {
            this.enableLogging = enable
        }

        fun setBaseUrl(baseUrl: String): Builder = apply {
            this.baseUrl = baseUrl
        }

        fun setThreeDSecureEnv(threeDSecureEnv: CardinalEnvironment): Builder = apply {
            this.threeDSecureEnv = threeDSecureEnv
        }

        fun build(): AirwallexConfiguration {
            return AirwallexConfiguration(
                enableLogging = enableLogging,
                baseUrl = baseUrl,
                threeDSecureEnv = threeDSecureEnv
            )
        }
    }
}
