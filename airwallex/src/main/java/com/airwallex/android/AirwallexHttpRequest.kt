package com.airwallex.android

import java.util.*
import kotlin.collections.HashMap

internal class AirwallexHttpRequest private constructor(builder: Builder) {

    enum class Method {
        GET, POST, PUT, DELETE;
    }

    class Builder {
        internal var url: String? = null
        internal var method: Method? = null
        internal var headers: MutableMap<String, String>
        internal var body: AirwallexHttpBody? = null

        init {
            headers = HashMap()
        }

        constructor()

        constructor(request: AirwallexHttpRequest) {
            url = request.url
            method = request.method
            headers = HashMap(request.allHeaders)
            body = request.body
        }

        fun setUrl(url: String?): Builder {
            this.url = url
            return this
        }

        fun setMethod(method: Method?): Builder {
            this.method = method
            return this
        }

        fun setBody(body: AirwallexHttpBody?): Builder {
            this.body = body
            return this
        }

        fun addHeader(name: String, value: String): Builder {
            headers[name] = value
            return this
        }

        fun addHeaders(headers: Map<String, String>?): Builder {
            this.headers.putAll(headers!!)
            return this
        }

        fun build(): AirwallexHttpRequest {
            return AirwallexHttpRequest(this)
        }
    }

    val url: String?
    val method: Method?
    val allHeaders: Map<String, String>
    private val body: AirwallexHttpBody?

    fun getHeader(name: String?): String? {
        return allHeaders[name]
    }

    fun getBody(): AirwallexHttpBody? {
        return body
    }

    init {
        url = builder.url
        method = builder.method
        allHeaders = Collections.unmodifiableMap(HashMap(builder.headers))
        body = builder.body
    }
}