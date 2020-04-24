package com.airwallex.android

import java.io.IOException
import java.nio.charset.StandardCharsets
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.BufferedSink

/**
 * Http client that wraps OkHttpClient
 */
internal class AirwallexHttpClient(val builder: OkHttpClient.Builder) {

    private val okHttpClient: OkHttpClient = builder.build()

    companion object {
        fun createClient(builder: OkHttpClient.Builder): AirwallexHttpClient {
            return AirwallexHttpClient(builder)
        }
    }

    /**
     * Execute the Airwallex API by okhttp
     *
     * @return a [AirwallexHttpResponse] from Airwallex server
     */
    @Throws(IOException::class)
    fun execute(parseRequest: AirwallexHttpRequest): AirwallexHttpResponse {
        val request = getRequest(parseRequest)
        val call = okHttpClient.newCall(request)
        val response = call.execute()
        return getResponse(response)
    }

    private fun getResponse(response: Response): AirwallexHttpResponse {
        val headers: MutableMap<String, String?> = mutableMapOf()
        for (name in response.headers.names()) {
            headers[name] = response.header(name)
        }
        return AirwallexHttpResponse.Builder()
            .setStatusCode(response.code)
            .setIsSuccessful(response.isSuccessful)
            .setBody(response.body)
            .setMessage(response.message)
            .setHeaders(headers)
            .build()
    }

    private fun getRequest(request: AirwallexHttpRequest): Request {
        val builder = Request.Builder()
        val method: AirwallexHttpRequest.Method? = request.method
        // Set method
        if (method == AirwallexHttpRequest.Method.GET) {
            builder.get()
        }

        // Set url
        builder.url(request.url)

        // Set Header
        val okHttpHeadersBuilder = Headers.Builder()
        for ((key, value) in request.allHeaders) {
            okHttpHeadersBuilder.add(key, value)
        }

        // OkHttp automatically add gzip header so we do not need to deal with it
        builder.headers(okHttpHeadersBuilder.build())

        // Set Body
        val parseBody: AirwallexHttpRequest.AirwallexHttpRequestBody? = request.body
        if (parseBody != null) {
            val okHttpRequestBody = AirwallexOkHttpRequestBody(parseBody)
            when (method) {
                AirwallexHttpRequest.Method.GET -> {
                    // No body for get request
                }
                AirwallexHttpRequest.Method.PUT -> builder.put(okHttpRequestBody)
                AirwallexHttpRequest.Method.POST -> builder.post(okHttpRequestBody)
                AirwallexHttpRequest.Method.DELETE -> builder.delete(okHttpRequestBody)
            }
        }

        return builder.build()
    }

    private class AirwallexOkHttpRequestBody internal constructor(val body: AirwallexHttpRequest.AirwallexHttpRequestBody) :
        RequestBody() {
        private val content: ByteArray = body.content.toByteArray(StandardCharsets.UTF_8)
        private val offset = 0
        private val byteCount = content.size

        override fun contentLength(): Long {
            return byteCount.toLong()
        }

        override fun contentType(): MediaType? {
            return body.contentType.toMediaTypeOrNull()
        }

        @Throws(IOException::class)
        override fun writeTo(sink: BufferedSink) {
            sink.write(content, offset, byteCount)
        }
    }
}
