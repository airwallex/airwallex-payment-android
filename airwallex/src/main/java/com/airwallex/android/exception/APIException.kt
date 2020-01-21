package com.airwallex.android.exception

import com.airwallex.android.model.AirwallexError

class APIException(
    message: String?,
    traceId: String? = null,
    statusCode: Int,
    error: AirwallexError? = null,
    e: Throwable? = null
) : AirwallexException(error, message, traceId, statusCode, e) {
    internal companion object {
        @JvmSynthetic
        internal fun create(e: AirwallexException): APIException {
            return APIException(
                message = e.message,
                traceId = e.traceId,
                statusCode = e.statusCode,
                e = e
            )
        }
    }
}
