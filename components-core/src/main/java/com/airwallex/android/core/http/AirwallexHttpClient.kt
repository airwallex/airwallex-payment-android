package com.airwallex.android.core.http

import com.airwallex.android.core.exception.APIConnectionException
import com.airwallex.android.core.exception.InvalidRequestException
import com.airwallex.android.core.log.AirwallexLogger
import java.io.IOException
import java.net.URL
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection

class AirwallexHttpClient {

    @Throws(IOException::class, InvalidRequestException::class)
    fun execute(request: AirwallexHttpRequest): AirwallexHttpResponse {
        AirwallexLogger.info(
            "AirwallexHttpClient request:url = ${request.url}",
            sensitiveMessage = request.toString()
        )
        AirwallexHttpConnection(
            (URL(request.url).openConnection() as HttpsURLConnection).apply {
                connectTimeout = CONNECT_TIMEOUT
                readTimeout = READ_TIMEOUT
                useCaches = false
                requestMethod = request.method.code

                request.headers.forEach { (key, value) ->
                    setRequestProperty(key, value)
                }

                if (AirwallexHttpRequest.Method.POST == request.method) {
                    doOutput = true
                    setRequestProperty(HEADER_CONTENT_TYPE, request.contentType)
                    outputStream.use { output -> request.writeBody(output) }
                }
            }
        ).use {
            try {
                val response = it.response
                AirwallexLogger.info(
                    "AirwallexHttpClient response: success, url = ${request.url}",
                    sensitiveMessage = response.toString()
                )
                return response
            } catch (e: IOException) {
                AirwallexLogger.error(
                    "AirwallexHttpClient response: failed, url = ${request.url}",
                    e
                )
                throw APIConnectionException.create(e, request.url)
            }
        }
    }

    private companion object {
        private val CONNECT_TIMEOUT = TimeUnit.SECONDS.toMillis(30).toInt()
        private val READ_TIMEOUT = TimeUnit.SECONDS.toMillis(80).toInt()

        private const val HEADER_CONTENT_TYPE = "Content-Type"
    }
}
