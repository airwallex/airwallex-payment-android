package com.airwallex.android.core.exception

import com.airwallex.android.core.model.AirwallexError
import java.io.IOException

/**
 * An exception that represents a failure to connect to Airwallex's API.
 */
class APIConnectionException(
    message: String? = null,
    e: Throwable
) : AirwallexException(AirwallexError(message = message), null, STATUS_CODE, message, e) {
    companion object {
        private const val STATUS_CODE = 0

        @JvmSynthetic
        fun create(e: IOException, url: String? = null): APIConnectionException {
            return APIConnectionException(
                "IOException during API request to $url: ${e.message}. Please check your internet connection and try again.",
                e
            )
        }
    }
}
