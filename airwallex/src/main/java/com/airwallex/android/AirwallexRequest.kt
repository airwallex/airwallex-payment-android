package com.airwallex.android

import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*

internal abstract class AirwallexRequest(
    val method: Method,
    val baseUrl: String,
    params: Map<String, *>?
) {


    private val params: Map<String, *>? = params?.let { compactParams(it) }

    internal enum class Method(val code: String) {
        GET("GET"),
        POST("POST"),
        DELETE("DELETE")
    }

    internal abstract fun getOutputBytes(): ByteArray

    companion object {

        private fun compactParams(params: Map<String, *>): Map<String, Any> {
            val compactParams = HashMap<String, Any>(params)

            // Remove all null values; they cause validation errors
            for (key in HashSet(compactParams.keys)) {
                when (val value = compactParams[key]) {
                    is CharSequence -> {
                        if (value.isEmpty()) {
                            compactParams.remove(key)
                        }
                    }
                    is Map<*, *> -> {
                        compactParams[key] = compactParams(value as Map<String, *>)
                    }
                    null -> {
                        compactParams.remove(key)
                    }
                }
            }

            return compactParams
        }
    }


    private data class Parameter internal constructor(
        internal val key: String,
        internal val value: String
    )

    private fun flattenParamsList(
        params: List<*>,
        keyPrefix: String
    ): List<Parameter> {
        // Because application/x-www-form-urlencoded cannot represent an empty
        // list, convention is to take the list parameter and just set it to an
        // empty string. (e.g. A regular list might look like `a[]=1&b[]=2`.
        // Emptying it would look like `a=`.)
        return if (params.isEmpty()) {
            listOf(Parameter(keyPrefix, ""))
        } else {
            val newPrefix = "$keyPrefix[]"
            params.flatMap {
                flattenParamsValue(it, newPrefix)
            }
        }
    }

    private fun flattenParamsMap(
        params: Map<String, *>?,
        keyPrefix: String?
    ): List<Parameter> {
        return params?.flatMap { (key, value) ->
            val newPrefix = keyPrefix?.let { "$it[$key]" } ?: key
            flattenParamsValue(value, newPrefix)
        }
            ?: emptyList()
    }

    private fun flattenParamsValue(
        value: Any?,
        keyPrefix: String
    ): List<Parameter> {
        return when (value) {
            is Map<*, *> -> flattenParamsMap(value as Map<String, Any>?, keyPrefix)
            is List<*> -> flattenParamsList(value, keyPrefix)
            "" -> throw Exception("11")
            null -> {
                listOf(Parameter(keyPrefix, ""))
            }
            else -> {
                listOf(Parameter(keyPrefix, value.toString()))
            }
        }
    }

    @Throws(UnsupportedEncodingException::class)
    private fun urlEncodePair(k: String, v: String): String {
        val encodedKey = urlEncode(k)
        val encodedValue = urlEncode(v)
        return "$encodedKey=$encodedValue"
    }

    @Throws(UnsupportedEncodingException::class)
    private fun urlEncode(str: String): String {
        // Preserve original behavior that passing null for an object id will lead
        // to us actually making a request to /v1/foo/null
        return URLEncoder.encode(str, ApiRequest.CHARSET)
    }

    private fun flattenParams(params: Map<String, *>?): List<Parameter> {
        return flattenParamsMap(params, null)
    }

    internal val query: String
        get() {
            return flattenParams(params).joinToString("&") {
                urlEncodePair(it.key, it.value)
            }
        }

}
