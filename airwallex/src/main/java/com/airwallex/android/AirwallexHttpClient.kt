package com.airwallex.android

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.BufferedSink
import java.io.IOException
import java.nio.charset.StandardCharsets

internal class AirwallexHttpClient(var builder: OkHttpClient.Builder?) {
    private val okHttpClient: OkHttpClient
    private var hasExecuted = false

    init {
        if (builder == null) {
            builder = OkHttpClient.Builder()
        }
        okHttpClient = builder!!.build()
    }

    companion object {
        fun createClient(builder: OkHttpClient.Builder?): AirwallexHttpClient {
            return AirwallexHttpClient(builder)
        }
    }

    @Throws(IOException::class)
    fun execute(request: AirwallexHttpRequest): AirwallexHttpResponse {
        if (!hasExecuted) {
            hasExecuted = true
        }
        return executeInternal(request)
    }

    /**
     * Execute internal. Keep default protection for tests
     *
     * @param parseRequest request
     * @return response
     * @throws IOException exception
     */
    @Throws(IOException::class)
    fun executeInternal(parseRequest: AirwallexHttpRequest): AirwallexHttpResponse {
        val okHttpRequest = getRequest(parseRequest)
        val okHttpCall = okHttpClient.newCall(okHttpRequest)
        val okHttpResponse = okHttpCall.execute()
        return getResponse(okHttpResponse)
    }

    fun getResponse(okHttpResponse: Response): AirwallexHttpResponse { // Status code
        val statusCode = okHttpResponse.code
        // Content
        val content = okHttpResponse.body!!.byteStream()
        // Total size
        val totalSize = okHttpResponse.body!!.contentLength()
        // Reason phrase
        val reasonPhrase = okHttpResponse.message
        // Headers
        val headers: MutableMap<String, String?> =
            java.util.HashMap()
        for (name in okHttpResponse.headers.names()) {
            headers[name] = okHttpResponse.header(name)
        }
        // Content type
        var contentType: String? = null
        val body = okHttpResponse.body
        if (body?.contentType() != null) {
            contentType = body.contentType().toString()
        }
        return AirwallexHttpResponse.Builder()
            .setStatusCode(statusCode)
            .setContent(content)
            .setTotalSize(totalSize)
            .setReasonPhrase(reasonPhrase)
            .setHeaders(headers)
            .setContentType(contentType)
            .build()
    }

    fun getRequest(request: AirwallexHttpRequest): Request {
        val okHttpRequestBuilder = Request.Builder()
        val method: AirwallexHttpRequest.Method = request.method!!
        when (method) {
            AirwallexHttpRequest.Method.GET -> okHttpRequestBuilder.get()
            AirwallexHttpRequest.Method.DELETE, AirwallexHttpRequest.Method.POST, AirwallexHttpRequest.Method.PUT -> {
            }
        }
        // Set url
        okHttpRequestBuilder.url(request.url!!)
        // Set Header
        val okHttpHeadersBuilder = Headers.Builder()
        for ((key, value) in request.allHeaders) {
            okHttpHeadersBuilder.add(key, value)
        }
        // OkHttp automatically add gzip header so we do not need to deal with it
        val okHttpHeaders = okHttpHeadersBuilder.build()
        okHttpRequestBuilder.headers(okHttpHeaders)
        // Set Body
        val parseBody: AirwallexHttpBody? = request.getBody()
        var okHttpRequestBody: AirwallexOkHttpRequestBody? = null
        if (parseBody != null) {
            okHttpRequestBody = AirwallexOkHttpRequestBody(parseBody)
        }
        when (method) {
            AirwallexHttpRequest.Method.PUT -> okHttpRequestBuilder.put(okHttpRequestBody!!)
            AirwallexHttpRequest.Method.POST -> okHttpRequestBuilder.post(okHttpRequestBody!!)
            AirwallexHttpRequest.Method.DELETE -> okHttpRequestBuilder.delete(okHttpRequestBody)
        }
        return okHttpRequestBuilder.build()
    }

    private class AirwallexOkHttpRequestBody internal constructor(val body: AirwallexHttpBody) :
        RequestBody() {
        private val content: ByteArray =
            body.content.toByteArray(StandardCharsets.UTF_8)
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