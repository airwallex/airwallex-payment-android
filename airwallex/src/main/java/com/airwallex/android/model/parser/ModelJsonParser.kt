package com.airwallex.android.model.parser

import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

internal interface ModelJsonParser<Model> {
    fun parse(json: JSONObject): Model?

    val dateFormat: SimpleDateFormat
        get() = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ", Locale.getDefault())

    companion object {
        internal fun jsonArrayToList(jsonArray: JSONArray?): List<String> {
            return jsonArray?.let {
                (0 until jsonArray.length()).map { jsonArray.getString(it) }
            } ?: emptyList()
        }
    }
}
