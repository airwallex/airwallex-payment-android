package com.airwallex.android

enum class Environment {
    DEMO, PRODUCTION;

    fun baseUrl(): String {
        return when (this) {
            DEMO -> "https://pci-api-demo.airwallex.com"
            PRODUCTION -> "https://pci-api.airwallex.com"
        }
    }

    fun cybsUrl(): String {
        return when (this) {
            DEMO -> "https://pci-api-demo.airwallex.com/pa/webhook/cybs"
            PRODUCTION -> "https://pci-api.airwallex.com/pa/webhook/cybs"
        }
    }
}
