package com.airwallex.android

import com.airwallex.android.exception.APIException
import org.json.JSONException
import org.json.JSONObject
import kotlin.jvm.Throws

internal data class AirwallexHttpResponse internal constructor(
    internal val code: Int,
    internal val body: String?,
    internal val headers: Map<String, List<String>> = emptyMap()
) {
    internal val isError: Boolean = code < 200 || code >= 300

    internal val traceId: String? = TRACE_ID_HEADER.getHeaderValue()?.firstOrNull()

    internal val responseJson: JSONObject
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
