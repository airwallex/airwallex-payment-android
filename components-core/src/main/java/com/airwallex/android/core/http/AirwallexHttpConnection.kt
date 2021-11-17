package com.airwallex.android.core.http

import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.*
import javax.net.ssl.HttpsURLConnection

internal class AirwallexHttpConnection internal constructor(private val conn: HttpsURLConnection) : Closeable {

    val response: AirwallexHttpResponse
        @Throws(IOException::class)
        get() {
            val responseCode = conn.responseCode
            return AirwallexHttpResponse(
                code = responseCode,
                body = responseBody,
                headers = conn.headerFields
            )
        }

    private val responseBody: String?
        @Throws(IOException::class)
        get() {
            return getResponseBody(responseStream)
        }

    private val responseStream: InputStream?
        @Throws(IOException::class)
        get() {
            return if (conn.responseCode in 200..299) {
                conn.inputStream
            } else {
                conn.errorStream
            }
        }

    @Throws(IOException::class)
    private fun getResponseBody(responseStream: InputStream?): String? {
        if (responseStream == null) {
            return null
        }

        val scanner = Scanner(responseStream, CHARSET).useDelimiter("\\A")
        val responseBody = if (scanner.hasNext()) {
            scanner.next()
        } else {
            null
        }
        responseStream.close()
        return responseBody
    }

    override fun close() {
        responseStream?.close()
        conn.disconnect()
    }

    private companion object {
        private val CHARSET = StandardCharsets.UTF_8.name()
    }
}
