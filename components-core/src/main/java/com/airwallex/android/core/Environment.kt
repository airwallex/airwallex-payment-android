package com.airwallex.android.core

enum class Environment(val value: String) {
    STAGING("staging"), DEMO("demo"), PRODUCTION("production");

    fun baseUrl(): String {
        return when (this) {
            STAGING -> "https://pci-api-staging.airwallex.com"
            DEMO -> "https://pci-api-demo.airwallex.com"
            PRODUCTION -> "https://pci-api.airwallex.com"
        }
    }

    fun cybsUrl(): String {
        return when (this) {
            STAGING -> "https://pci-api-staging.airwallex.com/pa/webhook/cybs"
            DEMO -> "https://pci-api-demo.airwallex.com/pa/webhook/cybs"
            PRODUCTION -> "https://pci-api.airwallex.com/pa/webhook/cybs"
        }
    }

    fun trackerUrl(): String {
        return when (this) {
            STAGING -> "https://pci-api-staging.airwallex.com/api/v1/checkout"
            DEMO -> "https://pci-api-demo.airwallex.com/api/v1/checkout"
            PRODUCTION -> "https://pci-api.airwallex.com/api/v1/checkout"
        }
    }

    fun threeDsReturnUrl(): String {
        return when (this) {
            STAGING -> "https://pci-api-staging.airwallex.com/api/v1/checkout/elements/3ds?origin=https://checkout-staging.airwallex.com"
            DEMO -> "https://pci-api-demo.airwallex.com/api/v1/checkout/elements/3ds?origin=https://checkout-demo.airwallex.com"
            PRODUCTION -> "https://pci-api.airwallex.com/api/v1/checkout/elements/3ds?origin=https://checkout.airwallex.com"
        }
    }
}
