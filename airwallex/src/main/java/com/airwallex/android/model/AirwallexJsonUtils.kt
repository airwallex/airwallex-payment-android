package com.airwallex.android.model

import androidx.annotation.Size
import org.json.JSONArray
import org.json.JSONObject

internal object AirwallexJsonUtils {
    private const val NULL = "null"

    @JvmSynthetic
    internal fun optBoolean(
        jsonObject: JSONObject,
        @Size(min = 1) fieldName: String
    ): Boolean {
        return jsonObject.has(fieldName) && jsonObject.optBoolean(fieldName, false)
    }

    @JvmSynthetic
    internal fun optDouble(
        jsonObject: JSONObject,
        @Size(min = 1) fieldName: String
    ): Double? {
        return if (!jsonObject.has(fieldName)) {
            null
        } else {
            jsonObject.optDouble(fieldName)
        }
    }

    @JvmSynthetic
    internal fun optLong(
        jsonObject: JSONObject,
        @Size(min = 1) fieldName: String
    ): Long? {
        return if (!jsonObject.has(fieldName)) {
            null
        } else {
            jsonObject.optLong(fieldName)
        }
    }

    @JvmStatic
    fun optString(
        jsonObject: JSONObject?,
        @Size(min = 1) fieldName: String
    ): String? {
        return nullIfNullOrEmpty(jsonObject?.optString(fieldName))
    }

    @JvmSynthetic
    internal fun optMap(
        jsonObject: JSONObject,
        @Size(min = 1) fieldName: String
    ): Map<String, Any?>? {
        return jsonObject.optJSONObject(fieldName)?.let {
            jsonObjectToMap(it)
        }
    }

    @JvmSynthetic
    internal fun jsonObjectToMap(jsonObject: JSONObject?): Map<String, Any?>? {
        if (jsonObject == null) {
            return null
        }

        val keys = jsonObject.names() ?: JSONArray()
        return (0 until keys.length())
            .map { idx -> keys.getString(idx) }
            .mapNotNull { key ->
                jsonObject.opt(key)?.let { value ->
                    if (value != NULL) {
                        mapOf(
                            key to
                                when (value) {
                                    is JSONObject -> jsonObjectToMap(value)
                                    is JSONArray -> jsonArrayToList(value)
                                    else -> value
                                }
                        )
                    } else {
                        null
                    }
                }
            }
            .fold(emptyMap()) { acc, map -> acc.plus(map) }
    }

    @JvmSynthetic
    internal fun jsonArrayToList(jsonArray: JSONArray?): List<Any>? {
        if (jsonArray == null) {
            return null
        }

        return (0 until jsonArray.length())
            .map { idx -> jsonArray.get(idx) }
            .mapNotNull { ob ->
                if (ob is JSONArray) {
                    jsonArrayToList(ob)
                } else if (ob is JSONObject) {
                    jsonObjectToMap(ob)
                } else {
                    if (ob == NULL) {
                        null
                    } else {
                        ob
                    }
                }
            }
    }

    @JvmSynthetic
    internal fun nullIfNullOrEmpty(possibleNull: String?): String? {
        return possibleNull?.let { s ->
            s.takeUnless { NULL == it || it.isEmpty() }
        }
    }
}
