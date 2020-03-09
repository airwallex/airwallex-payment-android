package com.airwallex.android

import java.util.*
import okhttp3.ResponseBody

internal class AirwallexHttpResponse private constructor(
    val isSuccessful: Boolean,
    val statusCode: Int,
    val body: ResponseBody?,
    val reason: String?,
    val allHeaders: MutableMap<String, String?>
) {

    class Builder {
        private var isSuccessful = false
        private var statusCode = 0
        private var body: ResponseBody? = null
        private var reason: String? = null
        private var headers: MutableMap<String, String?> = mutableMapOf()

        fun setIsSuccessful(isSuccessful: Boolean): Builder = apply {
            this.isSuccessful = isSuccessful
        }

        fun setStatusCode(statusCode: Int): Builder = apply {
            this.statusCode = statusCode
        }

        fun setBody(body: ResponseBody?): Builder = apply {
            this.body = body
        }

        fun setReason(reason: String?): Builder = apply {
            this.reason = reason
        }

        fun setHeaders(headers: MutableMap<String, String?>): Builder = apply {
            this.headers = HashMap(headers)
        }

        fun build(): AirwallexHttpResponse {
            return AirwallexHttpResponse(
                isSuccessful = isSuccessful,
                statusCode = statusCode,
                body = body,
                reason = reason,
                allHeaders = headers
            )
        }
    }
}
