package com.airwallex.android.core.http

import com.airwallex.android.core.AirwallexPlugins.AIRWALLEX_USER_AGENT
import com.airwallex.android.core.BuildConfig
import com.airwallex.android.core.exception.InvalidRequestException
import com.airwallex.android.core.model.Options
import com.airwallex.android.core.util.AirwallexJsonUtils
import java.io.OutputStream
import java.io.UnsupportedEncodingException

open class AirwallexHttpRequest internal constructor(
    val method: Method,
    val url: String,
    val params: Map<String, *>? = null,
    val options: Options?,
    private val awxTracker: String? = null,
    private val accept: String? = null
) {
    private val mimeType: MimeType = MimeType.Json

    internal open val contentType: String
        get() {
            return "${mimeType.code}; charset=$CHARSET"
        }

    internal val headers: Map<String, String>
        get() {
            return mapOf(
                ACCEPT_HEADER_KEY to (accept ?: ACCEPT_HEADER_VALUE),
                CONTENT_TYPE_HEADER_KEY to CONTENT_TYPE_HEADER_VALUE,
                USER_AGENT_KEY to USER_AGENT_VALUE,
                API_VERSION to BuildConfig.API_VERSION
            )
                .plus(
                    options?.clientSecret?.let {
                        it.takeIf { it.isNotEmpty() }?.let { secret ->
                            mapOf(CLIENT_SECRET to secret)
                        }
                    }.orEmpty()
                )
                .plus(
                    awxTracker?.let {
                        mapOf(AWX_TRACKER to it)
                    }.orEmpty()
                )
        }

    private val body: String
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

    enum class Method(val code: String) {
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

    companion object {
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

        fun createGet(
            url: String,
            options: Options?,
            params: Map<String, *>? = null,
            awxTracker: String? = null,
            accept: String? = null
        ): AirwallexHttpRequest {
            return AirwallexHttpRequest(Method.GET, url, params, options, awxTracker, accept)
        }

        fun createPost(
            url: String,
            options: Options?,
            params: Map<String, *>? = null
        ): AirwallexHttpRequest {
            return AirwallexHttpRequest(Method.POST, url, params, options)
        }
    }
}
