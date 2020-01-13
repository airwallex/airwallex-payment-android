package com.airwallex.android

internal class ApiKeyValidator {

    internal companion object {

        fun requireValid(apiKey: String?) {
            require(
                !apiKey.isNullOrBlank()
                        || apiKey?.length == 96
            ) {
                "Invalid Airwallex API Key: " +
                        "You must use a valid Airwallex API key to make a Airwallex API request. " +
                        "You can get the Airwallex API key from here " +
                        "https://www.airwallex.com/app/settings/api"
            }
        }
    }
}
