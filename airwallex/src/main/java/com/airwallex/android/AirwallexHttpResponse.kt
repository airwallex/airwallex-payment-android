package com.airwallex.android

import java.io.InputStream
import java.util.*

internal class AirwallexHttpResponse private constructor(builder: Builder) {

    val statusCode = builder.statusCode
    val content = builder.content
    val totalSize = builder.totalSize
    val reasonPhrase = builder.reasonPhrase
    val allHeaders = builder.headers
    val contentType = builder.contentType

    class Builder {
        internal var statusCode = 0
        internal var content: InputStream? = null
        internal var totalSize: Long = 0
        internal var reasonPhrase: String? = null
        internal var headers: MutableMap<String, String?> = mutableMapOf()
        internal var contentType: String? = null

        fun setStatusCode(statusCode: Int): Builder = apply {
            this.statusCode = statusCode
        }

        fun setContent(content: InputStream?): Builder = apply {
            this.content = content
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