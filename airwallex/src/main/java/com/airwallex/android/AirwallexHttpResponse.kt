package com.airwallex.android

import java.util.*
import okhttp3.ResponseBody

internal class AirwallexHttpResponse private constructor(
    val isSuccessful: Boolean,
    val statusCode: Int,
    val body: ResponseBody?,
    val message: String?,
    val allHeaders: MutableMap<String, String?>
) {

    class Builder {
        private var isSuccessful = false
        private var statusCode = 0
        private var body: ResponseBody? = null
        private var message: String? = null
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

        fun setMessage(message: String?): Builder = apply {
            this.message = message
        }

        fun setHeaders(headers: MutableMap<String, String?>): Builder = apply {
            this.headers = HashMap(headers)
        }

        fun build(): AirwallexHttpResponse {
            return AirwallexHttpResponse(
                isSuccessful = isSuccessful,
                statusCode = statusCode,
                body = body,
                message = message,
                allHeaders = headers
            )
        }
    }
}
