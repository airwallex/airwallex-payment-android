package com.airwallex.android.core.exception

import com.airwallex.android.core.model.AirwallexError

/**
 * Parent class for exceptions encountered when using the SDK.
 */
abstract class AirwallexException @JvmOverloads constructor(
    val error: AirwallexError?,
    private val traceId: String?,
    val statusCode: Int,
    message: String? = error?.message,
    e: Throwable? = null
) : Exception(message, e) {

    override fun toString(): String {
        val statusCodeStr = "; status-code: $statusCode"
        val reqIdStr: String = if (traceId != null) {
            "$statusCodeStr; request-id: $traceId"
        } else {
            statusCodeStr
        }
        return "${super.toString() + reqIdStr}, $error"
    }
}
