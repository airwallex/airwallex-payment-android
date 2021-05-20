package com.airwallex.android

import com.airwallex.android.Airwallex.Companion.AIRWALLEX_USER_AGENT
import com.airwallex.android.exception.InvalidRequestException
import com.airwallex.android.model.AirwallexJsonUtils
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import kotlin.jvm.Throws

internal open class AirwallexHttpRequest internal constructor(
    val method: Method,
    val url: String,
    val params: Map<String, *>? = null,
    val options: ApiRepository.Options,
    private val awxTracker: String? = null
) {
    private val mimeType: MimeType = MimeType.Json

    internal open val contentType: String
        get() {
            return "${mimeType.code}; charset=$CHARSET"
        }

    internal val headers: Map<String, String>
        get() {
            return mapOf(
                ACCEPT_HEADER_KEY to ACCEPT_HEADER_VALUE,
                CONTENT_TYPE_HEADER_KEY to CONTENT_TYPE_HEADER_VALUE,
                USER_AGENT_KEY to USER_AGENT_VALUE,
                API_VERSION to BuildConfig.API_VERSION,
                CLIENT_SECRET to options.clientSecret
            )
                .plus(
                    if (options.clientSecret.isEmpty()) {
                        emptyMap()
                    } else {
                        mapOf(CLIENT_SECRET to options.clientSecret)
                    }
                )
                .plus(
                    awxTracker?.let {
                        mapOf(AWX_TRACKER to it)
                    }.orEmpty()
                )
        }

    protected val body: String
        @Throws(InvalidRequestException::class, UnsupportedEncodingException::class)
        get() {
            return AirwallexJsonUtils.mapToJsonObject(params).toString()
        }

    internal open fun writeBody(outputStream: OutputStream) {
        try {
            body.toByteArray(Charsets.UTF_8).let {
                outputStream.write(it)
                outputStream.flush()
            }
        } catch (e: UnsupportedEncodingException) {
            throw InvalidRequestException(
                message = "Unable to encode parameters to ${Charsets.UTF_8.name()}."
            )
        }
    }

    internal enum class Method(val code: String) {
        GET("GET"),
        POST("POST")
    }

    internal enum class MimeType(val code: String) {
        Json("application/json")
    }

    override fun toString(): String {
        return if (Method.POST == method) {
            "${method.code} $url \n $body"
        } else {
            "${method.code} $url"
        }
    }

    internal companion object {
        private val CHARSET = Charsets.UTF_8.name()
        private const val ACCEPT_HEADER_KEY = "Accept"
        private const val ACCEPT_HEADER_VALUE = "application/json"
        private const val CONTENT_TYPE_HEADER_KEY = "Content-Type"
        private const val CONTENT_TYPE_HEADER_VALUE = "application/json"
        private const val USER_AGENT_KEY = "User-Agent"
        private const val USER_AGENT_VALUE = AIRWALLEX_USER_AGENT
        private const val API_VERSION = "x-api-version"
        private const val CLIENT_SECRET = "client-secret"
        private const val AWX_TRACKER = "Awx-Tracker"

        fun createGet(url: String, options: ApiRepository.Options, params: Map<String, *>? = null, awxTracker: String? = null): AirwallexHttpRequest {
            return AirwallexHttpRequest(Method.GET, url, params, options, awxTracker)
        }

        fun createPost(url: String, options: ApiRepository.Options, params: Map<String, *>? = null): AirwallexHttpRequest {
            return AirwallexHttpRequest(Method.POST, url, params, options)
        }
    }
}
