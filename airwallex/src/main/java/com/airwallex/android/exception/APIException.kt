package com.airwallex.android.exception

import com.airwallex.android.model.AirwallexError

class APIException(
    message: String?,
    requestId: String? = null,
    statusCode: Int,
    airwallexError: AirwallexError? = null,
    e: Throwable? = null
) : AirwallexException(airwallexError, message, requestId, statusCode, e) {
    internal companion object {
        @JvmSynthetic
        internal fun create(e: AirwallexException): APIException {
            return APIException(
                message = e.message,
                requestId = e.requestId,
                statusCode = e.statusCode,
                e = e
            )
        }
    }
}
