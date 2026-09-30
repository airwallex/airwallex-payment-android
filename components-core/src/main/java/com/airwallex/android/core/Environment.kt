package com.airwallex.android.core

enum class Environment(val value: String) {
    STAGING("staging"),

    @Deprecated(
        message = "Demo environment is no longer supported. Use Environment.PREVIEW instead.",
        replaceWith = ReplaceWith("PREVIEW")
    )
    DEMO("demo"),
    PRODUCTION("production"),
    PREVIEW("preview");

    @Suppress("DEPRECATION")
    fun baseUrl(): String {
        return when (this) {
            STAGING -> "https://api-staging.airwallex.com"
            DEMO, PREVIEW -> "https://api.sandbox.airwallex.com"
            PRODUCTION -> "https://api.airwallex.com"
        }
    }

    @Suppress("DEPRECATION")
    fun threeDsReturnUrl(): String {
        return when (this) {
            STAGING -> "https://pci-api-staging.airwallex.com/api/v1/checkout/elements/3ds?origin=https://checkout-staging.airwallex.com"
            DEMO, PREVIEW -> "https://pci-api.sandbox.airwallex.com/api/v1/checkout/elements/3ds?origin=https://checkout.sandbox.airwallex.com"
            PRODUCTION -> "https://pci-api.airwallex.com/api/v1/checkout/elements/3ds?origin=https://checkout.airwallex.com"
        }
    }

    @Suppress("DEPRECATION")
    val riskEnvironment: com.airwallex.risk.Environment
        get() = when (this) {
            STAGING -> com.airwallex.risk.Environment.STAGING
            DEMO, PREVIEW -> com.airwallex.risk.Environment.PREVIEW
            PRODUCTION -> com.airwallex.risk.Environment.PRODUCTION
        }
}
