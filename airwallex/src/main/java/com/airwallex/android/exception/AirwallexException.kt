package com.airwallex.android.exception

import com.airwallex.android.model.AirwallexError

/**
 * Parent class for exceptions encountered when using the SDK.
 */
abstract class AirwallexException @JvmOverloads constructor(
    val error: AirwallexError?,
    message: String?,
    private val traceId: String?,
    private val statusCode: Int,
    e: Throwable? = null
) : Exception(message, e) {

    override fun toString(): String {
        val statusCodeStr = "; status-code: $statusCode"
        var reqIdStr: String = if (traceId != null) {
            "$statusCodeStr; request-id: $traceId"
        } else {
            statusCodeStr
        }

        reqIdStr = super.toString() + reqIdStr

        if (error != null) {
            reqIdStr = "$reqIdStr, $error"
        }
        return reqIdStr
    }
}
