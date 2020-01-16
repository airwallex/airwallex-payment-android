package com.airwallex.android

import java.io.InputStream
import java.util.*

internal class AirwallexHttpResponse private constructor(builder: Builder) {

    class Builder {
        internal var statusCode = 0
        internal var content: InputStream? = null
        internal var totalSize: Long = 0
        internal var reasonPhrase: String? = null
        internal var headers: MutableMap<String, String?>? = null
        var contentType: String? = null

        constructor() {
            totalSize = -1
            headers = HashMap()
        }

        constructor(response: AirwallexHttpResponse) : super() {
            setStatusCode(response.statusCode)
            setContent(response.content)
            setTotalSize(response.totalSize)
            setContentType(response.contentType)
            setHeaders(response.allHeaders)
            setReasonPhrase(response.reasonPhrase)
        }

        fun setStatusCode(statusCode: Int): Builder {
            this.statusCode = statusCode
            return this
        }

        fun setContent(content: InputStream?): Builder {
            this.content = content
            return this
        }

        fun setTotalSize(totalSize: Long): Builder {
            this.totalSize = totalSize
            return this
        }

        fun setReasonPhrase(reasonPhrase: String?): Builder {
            this.reasonPhrase = reasonPhrase
            return this
        }

        fun setHeaders(headers: MutableMap<String, String?>): Builder {
            this.headers = HashMap(headers)
            return this
        }

        fun addHeaders(headers: Map<String, String>?): Builder {
            this.headers!!.putAll(headers!!)
            return this
        }

        fun addHeader(name: String, value: String): Builder {
            headers!![name] = value
            return this
        }

        fun setContentType(contentType: String?): Builder {
            this.contentType = contentType
            return this
        }

        fun build(): AirwallexHttpResponse {
            return AirwallexHttpResponse(this)
        }
    }

    val statusCode: Int

    val content: InputStream?

    val totalSize: Long

    val reasonPhrase: String?

    val allHeaders: MutableMap<String, String?>

    val contentType: String?

    fun getHeader(name: String?): String? {
        return allHeaders[name]
    }

    init {
        statusCode = builder.statusCode
        content = builder.content
        totalSize = builder.totalSize
        reasonPhrase = builder.reasonPhrase
        allHeaders = Collections.unmodifiableMap(
            HashMap(builder.headers!!)
        )
        contentType = builder.contentType
    }
}