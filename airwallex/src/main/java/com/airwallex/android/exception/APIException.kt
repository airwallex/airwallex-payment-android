package com.airwallex.android.exception

import com.airwallex.android.model.AirwallexError

/**
 * An exception that represents an internal problem with Airwallex's servers.
 */
class APIException(
    message: String?,
    traceId: String? = null,
    statusCode: Int,
    error: AirwallexError? = null,
    e: Throwable? = null
) : AirwallexException(error, message, traceId, statusCode, e)
