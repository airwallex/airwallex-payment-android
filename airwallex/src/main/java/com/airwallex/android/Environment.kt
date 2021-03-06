package com.airwallex.android

enum class Environment {
    STAGING, DEMO, PRODUCTION;

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
}
