package com.airwallex.android.exception

import com.airwallex.android.model.AirwallexError

abstract class AirwallexException @JvmOverloads constructor(
    val airwallexError: AirwallexError?,
    message: String?,
    val requestId: String?,
    val statusCode: Int,
    e: Throwable? = null
) : Exception(message, e) {

    constructor(
        message: String?,
        requestId: String?,
        statusCode: Int,
        e: Throwable?
    ) : this(null, message, requestId, statusCode, e)

    override fun toString(): String {
        var reqIdStr: String = if (requestId != null) {
            "; request-id: $requestId"
        } else {
            ""
        }

        reqIdStr = super.toString() + reqIdStr

        if (airwallexError != null) {
            reqIdStr = "$reqIdStr, $airwallexError"
        }
        return reqIdStr
    }
}
