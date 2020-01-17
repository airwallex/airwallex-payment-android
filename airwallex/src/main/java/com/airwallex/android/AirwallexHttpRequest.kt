package com.airwallex.android

internal class AirwallexHttpRequest private constructor(builder: Builder) {

    val url = builder.url
    val method = builder.method
    val allHeaders = builder.headers
    val body = builder.body

    enum class Method {
        GET, POST, PUT, DELETE;
    }

    class Builder(val url: String, val method: Method) {
        internal var headers: MutableMap<String, String> = mutableMapOf()
        internal var body: AirwallexHttpBody? = null

        fun setBody(body: AirwallexHttpBody?): Builder = apply {
            this.body = body
        }

        fun addHeader(name: String, value: String): Builder = apply {
            headers[name] = value
        }

        fun build(): AirwallexHttpRequest {
            return AirwallexHttpRequest(this)
        }
    }
}