package com.airwallex.android

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.net.URLEncoder
import java.util.*

internal class ApiRequest internal constructor(
    method: Method,
    url: String,
    val options: Options,
    params: Map<String, *>? = null
) : AirwallexRequest(method, url, params) {

    internal companion object {

        const val CHARSET = "UTF-8"

        @JvmSynthetic
        internal fun createGet(
            url: String,
            options: Options,
            params: Map<String, *>? = null
        ): ApiRequest {
            return ApiRequest(Method.GET, url, options, params)
        }

        @JvmSynthetic
        internal fun createPost(
            url: String,
            options: Options,
            params: Map<String, *>? = null
        ): ApiRequest {
            return ApiRequest(Method.POST, url, options, params)
        }

        @JvmSynthetic
        internal fun createDelete(
            url: String,
            options: Options
        ): ApiRequest {
            return ApiRequest(Method.DELETE, url, options, null)
        }
    }


    private fun getPostDataString(params: HashMap<String, String>): String? {
        val result = StringBuilder()
        var first = true
        for ((key, value) in params) {
            if (first) first = false else result.append("&")
            result.append(URLEncoder.encode(key, "UTF-8"))
            result.append("=")
            result.append(URLEncoder.encode(value, "UTF-8"))
        }
        return result.toString()
    }

    override fun getUserAgent(): String {
        return DEFAULT_USER_AGENT
    }

    override fun createHeaders(): Map<String, String> {
        return mapOf("Authorization" to "Bearer ${options.token}")
    }

    override fun getOutputBytes(): ByteArray {
        return query.toByteArray(charset(CHARSET))
    }

    @Parcelize
    internal data class Options internal constructor(
        val token: String
    ) : Parcelable
}