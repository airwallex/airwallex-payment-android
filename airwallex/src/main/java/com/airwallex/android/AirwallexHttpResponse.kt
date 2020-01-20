package com.airwallex.android

import okhttp3.ResponseBody
import java.util.*

internal class AirwallexHttpResponse private constructor(
    val isSuccessful: Boolean,
    val statusCode: Int,
    val body: ResponseBody?,
    val totalSize: Long,
    val reasonPhrase: String?,
    val allHeaders: MutableMap<String, String?>,
    val contentType: String?
) {

    class Builder {
        private var isSuccessful = false
        private var statusCode = 0
        private var body: ResponseBody? = null
        private var totalSize: Long = 0
        private var reasonPhrase: String? = null
        private var headers: MutableMap<String, String?> = mutableMapOf()
        private var contentType: String? = null

        fun setIsSuccessful(isSuccessful: Boolean): Builder = apply {
            this.isSuccessful = isSuccessful
        }

        fun setStatusCode(statusCode: Int): Builder = apply {
            this.statusCode = statusCode
        }

        fun setBody(body: ResponseBody?): Builder = apply {
            this.body = body
        }

        fun setTotalSize(totalSize: Long): Builder = apply {
            this.totalSize = totalSize
        }

        fun setReasonPhrase(reasonPhrase: String?): Builder = apply {
            this.reasonPhrase = reasonPhrase
        }

        fun setHeaders(headers: MutableMap<String, String?>): Builder = apply {
            this.headers = HashMap(headers)
        }

        fun setContentType(contentType: String?): Builder = apply {
            this.contentType = contentType
        }

        fun build(): AirwallexHttpResponse {
            return AirwallexHttpResponse(
                isSuccessful = isSuccessful,
                statusCode = statusCode,
                body = body,
                totalSize = totalSize,
                reasonPhrase = reasonPhrase,
                allHeaders = headers,
                contentType = contentType
            )
        }
    }
}