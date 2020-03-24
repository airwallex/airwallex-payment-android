package com.airwallex.android

import com.airwallex.android.exception.AirwallexException

internal data class ResponseWrapper<Response> internal constructor(
    val response: Response? = null,
    val exception: AirwallexException? = null
) {
    internal companion object {
        @JvmSynthetic
        internal fun <Response> create(response: Response?): ResponseWrapper<Response> {
            return ResponseWrapper(response = response)
        }

        @JvmSynthetic
        internal fun <Response> create(error: AirwallexException): ResponseWrapper<Response> {
            return ResponseWrapper(exception = error)
        }
    }
}
