package com.airwallex.android.exception

abstract class AirwallexException @JvmOverloads constructor(
    val stripeError: AirwallexError?,
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
        val reqIdStr: String = if (requestId != null) {
            "; request-id: $requestId"
        } else {
            ""
        }
        return super.toString() + reqIdStr
    }

    internal companion object {
        protected const val serialVersionUID = 1L
    }
}
