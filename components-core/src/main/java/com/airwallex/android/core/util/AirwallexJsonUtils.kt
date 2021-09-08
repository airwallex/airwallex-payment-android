package com.airwallex.android.core.util

import androidx.annotation.Size
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

@Suppress("UNCHECKED_CAST")
object AirwallexJsonUtils {
    private const val NULL = "null"

    @JvmSynthetic
    fun optBoolean(
        jsonObject: JSONObject,
        @Size(min = 1) fieldName: String
    ): Boolean {
        return jsonObject.has(fieldName) && jsonObject.optBoolean(fieldName, false)
    }

    @JvmSynthetic
    fun optDouble(
        jsonObject: JSONObject,
        @Size(min = 1) fieldName: String
    ): Double? {
        return if (!jsonObject.has(fieldName)) {
            null
        } else {
            jsonObject.optDouble(fieldName)
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
    fun optMap(
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
    fun jsonArrayToList(jsonArray: JSONArray?): List<Any>? {
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

    internal fun mapToJsonObject(mapObject: Map<String, *>?): JSONObject? {
        if (mapObject == null) {
            return null
        }
        val jsonObject = JSONObject()
        for (key in mapObject.keys) {
            val value = mapObject[key] ?: continue

            try {
                if (value is Map<*, *>) {
                    try {
                        val mapValue = value as Map<String, Any>
                        jsonObject.put(key, mapToJsonObject(mapValue))
                    } catch (classCastException: ClassCastException) {
                    }
                } else if (value is List<*>) {
                    jsonObject.put(key, listToJsonArray(value as List<Any>))
                } else if (value is Number || value is Boolean) {
                    jsonObject.put(key, value)
                } else {
                    jsonObject.put(key, value.toString())
                }
            } catch (jsonException: JSONException) {
            }
        }
        return jsonObject
    }

    private fun listToJsonArray(values: List<*>?): JSONArray? {
        if (values == null) {
            return null
        }

        val jsonArray = JSONArray()
        values.forEach { objVal ->
            val jsonVal =
                if (objVal is Map<*, *>) {
                    mapToJsonObject(objVal as Map<String, Any>)
                } else if (objVal is List<*>) {
                    listToJsonArray(objVal)
                } else if (objVal is Number || objVal is Boolean) {
                    objVal
                } else {
                    objVal.toString()
                }
            jsonArray.put(jsonVal)
        }
        return jsonArray
    }
}
