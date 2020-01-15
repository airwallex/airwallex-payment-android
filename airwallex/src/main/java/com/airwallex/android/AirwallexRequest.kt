package com.airwallex.android

import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*

internal abstract class AirwallexRequest(
    val method: Method,
    val baseUrl: String,
    params: Map<String, *>?
) {

    internal enum class Method(val code: String) {
        GET("GET"),
        POST("POST"),
        DELETE("DELETE")
    }
    internal abstract fun getOutputBytes(): ByteArray

}
