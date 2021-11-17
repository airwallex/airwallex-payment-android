package com.airwallex.android.core.exception

import com.airwallex.android.core.model.AirwallexError

/**
 * An exception that represents an internal problem with Airwallex's sdk.
 */
class AirwallexCheckoutException(
    error: AirwallexError? = null,
    traceId: String? = null,
    statusCode: Int = 0,
    message: String? = error?.message,
    e: Throwable? = null
) : AirwallexException(error, traceId, statusCode, message, e)
