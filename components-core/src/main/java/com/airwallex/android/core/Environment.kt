package com.airwallex.android.core

enum class Environment(val value: String) {
    STAGING("staging"), DEMO("demo"), PRODUCTION("production"), PREVIEW("preview");

    fun baseUrl(): String {
        return when (this) {
            STAGING -> "https://api-staging.airwallex.com"
            DEMO -> "https://api-demo.airwallex.com"
            PRODUCTION -> "https://api.airwallex.com"
            PREVIEW -> "https://api.sandbox.airwallex.com"
        }
    }

    fun threeDsReturnUrl(): String {
        return when (this) {
            STAGING -> "https://pci-api-staging.airwallex.com/api/v1/checkout/elements/3ds?origin=https://checkout-staging.airwallex.com"
            DEMO -> "https://pci-api-demo.airwallex.com/api/v1/checkout/elements/3ds?origin=https://checkout-demo.airwallex.com"
            PRODUCTION -> "https://pci-api.airwallex.com/api/v1/checkout/elements/3ds?origin=https://checkout.airwallex.com"
            PREVIEW -> "https://pci-api.sandbox.airwallex.com/api/v1/checkout/elements/3ds?origin=https://checkout.sandbox.airwallex.com"
        }
    }

    val riskEnvironment: com.airwallex.risk.Environment
        get() = when (this) {
            STAGING -> com.airwallex.risk.Environment.STAGING
            DEMO -> com.airwallex.risk.Environment.DEMO
            PRODUCTION -> com.airwallex.risk.Environment.PRODUCTION
            PREVIEW -> com.airwallex.risk.Environment.PREVIEW
        }
}
