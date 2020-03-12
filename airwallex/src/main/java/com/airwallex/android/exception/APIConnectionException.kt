package com.airwallex.android.exception

import java.io.IOException

class APIConnectionException(
    message: String? = null,
    e: Throwable? = null
) : AirwallexException(null, message, null, STATUS_CODE, e) {
    internal companion object {
        private const val STATUS_CODE = 0

        @JvmSynthetic
        internal fun create(e: IOException, url: String? = null): APIConnectionException {
            return APIConnectionException(
                "IOException during API request to $url: ${e.message}. Please check your internet connection and try again.",
                e
            )
        }
    }
}
