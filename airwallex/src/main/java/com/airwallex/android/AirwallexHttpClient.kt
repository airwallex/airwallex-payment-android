package com.airwallex.android

import java.io.IOException
import java.nio.charset.StandardCharsets
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.BufferedSink

internal class AirwallexHttpClient(builder: OkHttpClient.Builder) {

    private val okHttpClient: OkHttpClient = builder.build()

    companion object {
        fun createClient(builder: OkHttpClient.Builder): AirwallexHttpClient {
            return AirwallexHttpClient(builder)
        }
    }

    @Throws(IOException::class)
    fun execute(parseRequest: AirwallexHttpRequest): AirwallexHttpResponse {
        val request = getRequest(parseRequest)
        val call = okHttpClient.newCall(request)
        val response = call.execute()
        return getResponse(response)
    }

    private fun getResponse(response: Response): AirwallexHttpResponse {
        // Status code
        val statusCode = response.code

        val isSuccessful = response.isSuccessful

        // Body
        val body = response.body

        // Reason
        val reason = response.message

        // Headers
        val headers: MutableMap<String, String?> = mutableMapOf()
        for (name in response.headers.names()) {
            headers[name] = response.header(name)
        }
        return AirwallexHttpResponse.Builder()
            .setStatusCode(statusCode)
            .setIsSuccessful(isSuccessful)
            .setBody(body)
            .setReason(reason)
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
        val okHttpHeaders = okHttpHeadersBuilder.build()
        builder.headers(okHttpHeaders)

        // Set Body
        val parseBody: AirwallexHttpBody? = request.body
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

    private class AirwallexOkHttpRequestBody internal constructor(val body: AirwallexHttpBody) :
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
