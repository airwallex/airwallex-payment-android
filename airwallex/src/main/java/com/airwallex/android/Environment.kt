package com.airwallex.android

enum class Environment {
    DEMO, PRODUCTION;

    fun baseUrl(): String {
        return when (this) {
            DEMO -> "https://demo-pci-api.airwallex.com"
            PRODUCTION -> "https://pci-api.airwallex.com"
        }
    }
}