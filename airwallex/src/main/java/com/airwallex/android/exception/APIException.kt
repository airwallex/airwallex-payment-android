package com.airwallex.android.exception

import com.airwallex.android.model.AirwallexError

/**
 * An exception that represents an internal problem with Airwallex's servers.
 */
class APIException(
    error: AirwallexError? = null,
    traceId: String? = null,
    statusCode: Int = 0,
    message: String? = error?.message,
    e: Throwable? = null
) : AirwallexException(error, traceId, statusCode, message, e)
