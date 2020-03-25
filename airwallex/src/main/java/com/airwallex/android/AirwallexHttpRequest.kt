package com.airwallex.android

/**
 * The http request we send to Airwallex server.
 */
internal class AirwallexHttpRequest private constructor(
    val url: String,
    val method: Method,
    val allHeaders: MutableMap<String, String>,
    val body: AirwallexHttpBody?
) {

    enum class Method {
        GET, POST, PUT, DELETE;
    }

    class Builder(private val url: String, private val method: Method) {
        private var headers: MutableMap<String, String> = mutableMapOf()
        private var body: AirwallexHttpBody? = null

        fun setBody(body: AirwallexHttpBody?): Builder = apply {
            this.body = body
        }

        fun addHeader(name: String, value: String): Builder = apply {
            headers[name] = value
        }

        fun build(): AirwallexHttpRequest {
            return AirwallexHttpRequest(
                url = url,
                method = method,
                allHeaders = headers,
                body = body
            )
        }
    }
}
