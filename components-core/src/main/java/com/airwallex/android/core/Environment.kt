package com.airwallex.android.core

enum class Environment(val value: String) {
    STAGING("staging"), DEMO("demo"), PRODUCTION("production");

    fun baseUrl(): String {
        return when (this) {
            STAGING -> "https://api-staging.airwallex.com"
            DEMO -> "https://api-demo.airwallex.com"
            PRODUCTION -> "https://api.airwallex.com"
        }
    }

    fun trackerUrl(): String {
        return when (this) {
            STAGING -> "https://api-staging.airwallex.com/api/v1/checkout"
            DEMO -> "https://api-demo.airwallex.com/api/v1/checkout"
            PRODUCTION -> "https://api.airwallex.com/api/v1/checkout"
        }
    }

    fun threeDsReturnUrl(): String {
        return when (this) {
            STAGING -> "https://pci-api-staging.airwallex.com/api/v1/checkout/elements/3ds?origin=https://checkout-staging.airwallex.com"
            DEMO -> "https://pci-api-demo.airwallex.com/api/v1/checkout/elements/3ds?origin=https://checkout-demo.airwallex.com"
            PRODUCTION -> "https://pci-api.airwallex.com/api/v1/checkout/elements/3ds?origin=https://checkout.airwallex.com"
        }
    }

    val riskEnvironment: com.airwallex.risk.Environment
        get() = when (this) {
            STAGING -> com.airwallex.risk.Environment.STAGING
            DEMO -> com.airwallex.risk.Environment.DEMO
            PRODUCTION -> com.airwallex.risk.Environment.PRODUCTION
        }
}
