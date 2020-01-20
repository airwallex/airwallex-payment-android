package com.airwallex.android

import okhttp3.ResponseBody
import java.io.InputStream
import java.util.*

internal class AirwallexHttpResponse private constructor(builder: Builder) {

    val isSuccessful = builder.isSuccessful
    val statusCode = builder.statusCode
    val body = builder.body
    val totalSize = builder.totalSize
    val reasonPhrase = builder.reasonPhrase
    val allHeaders = builder.headers
    val contentType = builder.contentType

    class Builder {
        internal var isSuccessful = false
        internal var statusCode = 0
        internal var body: ResponseBody? = null
        internal var totalSize: Long = 0
        internal var reasonPhrase: String? = null
        internal var headers: MutableMap<String, String?> = mutableMapOf()
        internal var contentType: String? = null

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
            return AirwallexHttpResponse(this)
        }
    }
}