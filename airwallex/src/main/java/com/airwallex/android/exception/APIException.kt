package com.airwallex.android.exception

import com.airwallex.android.model.AirwallexError

/**
 * An exception that represents an internal problem with Airwallex's servers.
 */
class APIException(
    error: AirwallexError,
    traceId: String? = null,
    statusCode: Int,
    message: String?,
    e: Throwable? = null
) : AirwallexException(error, traceId, statusCode, message, e)
