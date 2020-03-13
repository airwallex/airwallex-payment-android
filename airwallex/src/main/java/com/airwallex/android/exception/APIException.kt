package com.airwallex.android.exception

import com.airwallex.android.model.AirwallexError

class APIException(
    message: String?,
    traceId: String? = null,
    statusCode: Int,
    error: AirwallexError? = null,
    e: Throwable? = null
) : AirwallexException(error, message, traceId, statusCode, e)
