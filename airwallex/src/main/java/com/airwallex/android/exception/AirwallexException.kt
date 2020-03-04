package com.airwallex.android.exception

import com.airwallex.android.model.AirwallexError

abstract class AirwallexException @JvmOverloads constructor(
    val error: AirwallexError?,
    message: String?,
    val traceId: String?,
    val statusCode: Int,
    e: Throwable? = null
) : Exception(message, e) {

    override fun toString(): String {
        var reqIdStr: String = if (traceId != null) {
            "; request-id: $traceId"
        } else {
            ""
        }

        reqIdStr = super.toString() + reqIdStr

        if (error != null) {
            reqIdStr = "$reqIdStr, $error"
        }
        return reqIdStr
    }
}
