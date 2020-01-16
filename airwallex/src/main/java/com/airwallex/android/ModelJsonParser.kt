package com.airwallex.android

import org.json.JSONArray
import org.json.JSONObject

internal interface ModelJsonParser<out ModelType : AirwallexModel> {
    fun parse(json: JSONObject): ModelType?

    companion object {
        internal fun jsonArrayToList(jsonArray: JSONArray?): List<String> {
            return jsonArray?.let {
                (0 until jsonArray.length()).map { jsonArray.getString(it) }
            } ?: emptyList()
        }
    }
}
