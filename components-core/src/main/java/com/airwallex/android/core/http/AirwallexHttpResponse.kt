package com.airwallex.android.core.http

import com.airwallex.android.core.exception.APIException
import org.json.JSONException
import org.json.JSONObject

data class AirwallexHttpResponse internal constructor(
    val code: Int,
    internal val body: String?,
    internal val headers: Map<String, List<String>> = emptyMap()
) {
    val isError: Boolean = code < 200 || code >= 300

    val traceId: String? = TRACE_ID_HEADER.getHeaderValue()?.firstOrNull()

    val responseJson: JSONObject
        @Throws(APIException::class)
        get() {
            return body?.let {
                try {
                    JSONObject(it)
                } catch (e: JSONException) {
                    throw APIException(
                        message = "Exception while parsing response body. Status code: $code Trace-Id: $traceId",
                        e = e
                    )
                }
            } ?: JSONObject()
        }

    private fun String.getHeaderValue(): List<String>? {
        return headers.entries.firstOrNull {
            it.key.equals(this, ignoreCase = true)
        }?.value
    }

    override fun toString(): String {
        return "Status Code: $code, Trace-Id: $traceId \n $body"
    }

    private companion object {
        private const val TRACE_ID_HEADER = "x-awx-traceid"
    }
}
