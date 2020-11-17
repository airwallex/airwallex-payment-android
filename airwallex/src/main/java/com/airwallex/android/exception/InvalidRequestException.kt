package com.airwallex.android.exception

import com.airwallex.android.model.AirwallexError

class InvalidRequestException(
    val param: String? = null,
    error: AirwallexError? = null,
    traceId: String? = null,
    statusCode: Int = 0,
    message: String? = null,
    e: Throwable? = null
) : AirwallexException(error, traceId, statusCode, message, e)
