package com.airwallex.android

/**
 * The http request we send to Airwallex server.
 */
internal class AirwallexHttpRequest private constructor(
    val url: String,
    val method: Method,
    val allHeaders: MutableMap<String, String>,
    val body: AirwallexHttpRequestBody?
) {

    /**
     * A data class that contains http request body details
     */
    internal data class AirwallexHttpRequestBody(
        val contentType: String,
        val content: String
    ) {
        override fun toString(): String {
            return "contentType $contentType, content $content"
        }
    }

    /**
     * The method type of [AirwallexHttpRequest]
     */
    internal enum class Method {
        GET, POST, PUT, DELETE;
    }

    /**
     * Builder of [AirwallexHttpRequest]
     */
    internal class Builder(private val url: String, private val method: Method) {
        private var headers: MutableMap<String, String> = mutableMapOf()
        private var body: AirwallexHttpRequestBody? = null

        fun setBody(body: AirwallexHttpRequestBody?): Builder = apply {
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

    override fun toString(): String {
        return "\nmethod: $method" +
                "\nurl: $url" +
                "\nallHeaders: $allHeaders" +
                "\nbody: $body"
    }
}
