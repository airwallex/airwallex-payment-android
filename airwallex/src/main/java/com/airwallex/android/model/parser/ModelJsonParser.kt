package com.airwallex.android.model.parser

import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

internal interface ModelJsonParser<Model> {
    fun parse(json: JSONObject): Model?

    val dateFormat: SimpleDateFormat
        get() = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ", Locale.getDefault())
}
