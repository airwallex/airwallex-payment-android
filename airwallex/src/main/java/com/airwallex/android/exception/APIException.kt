package com.airwallex.android.exception

class APIException(
    message: String?,
    requestId: String? = null,
    statusCode: Int,
    stripeError: AirwallexError? = null,
    e: Throwable? = null
) : AirwallexException(stripeError, message, requestId, statusCode, e) {
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
