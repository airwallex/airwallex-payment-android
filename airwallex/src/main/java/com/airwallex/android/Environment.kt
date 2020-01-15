package com.airwallex.android

enum class Environment(val authUrl: String, val baseUrl: String) {
    DEV(
        "https://api-staging.airwallex.com",
        "https://staging-pci-api.airwallex.com"
    ),
    STAGING(
        "https://api-staging.airwallex.com",
        "https://staging-pci-api.airwallex.com"
    ),
    QA(
        "https://api-staging.airwallex.com",
        "https://staging-pci-api.airwallex.com"
    ),
    PRODUCTION(
        "https://api-staging.airwallex.com",
        "https://staging-pci-api.airwallex.com"
    );

    companion object {

        internal fun getEnvironment(value: Int): Environment {
            return when (value) {
                0 -> DEV
                1 -> STAGING
                2 -> QA
                3 -> PRODUCTION
                else -> PRODUCTION
            }
        }
    }
}