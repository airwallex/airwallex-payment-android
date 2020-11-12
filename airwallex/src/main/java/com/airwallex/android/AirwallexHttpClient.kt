package com.airwallex.android

import com.airwallex.android.exception.APIConnectionException
import com.airwallex.android.exception.InvalidRequestException
import java.io.IOException
import java.net.URL
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection
import kotlin.jvm.Throws

internal class AirwallexHttpClient {

    @Throws(IOException::class, InvalidRequestException::class)
    fun execute(request: AirwallexHttpRequest): AirwallexHttpResponse {
        Logger.info(request.toString())
        AirwallexHttpConnection((URL(request.url).openConnection() as HttpsURLConnection).apply {
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
        }).use {
            try {
                val response = it.response
                Logger.info(response.toString())
                return response
            } catch (e: IOException) {
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
