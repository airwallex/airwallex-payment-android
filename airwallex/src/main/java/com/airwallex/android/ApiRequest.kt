package com.airwallex.android

import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*


internal class ApiRequest internal constructor(
    method: Method,
    url: String,
    params: Map<String, *>? = null
) : AirwallexRequest(method, url, params) {

    internal companion object {

        const val HEADER_USER_AGENT = "User-Agent"
        const val CHARSET = "UTF-8"


        @JvmSynthetic
        internal fun createGet(
            url: String,
            params: Map<String, *>? = null
        ): ApiRequest {
            return ApiRequest(Method.GET, url, params)
        }

        @JvmSynthetic
        internal fun createPost(
            url: String,
            params: Map<String, *>? = null
        ): ApiRequest {
            return ApiRequest(Method.POST, url, params)
        }

        @JvmSynthetic
        internal fun createDelete(
            url: String
        ): ApiRequest {
            return ApiRequest(Method.DELETE, url, null)
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

    override fun getOutputBytes(): ByteArray {
        return query.toByteArray(charset(CHARSET))
    }

}