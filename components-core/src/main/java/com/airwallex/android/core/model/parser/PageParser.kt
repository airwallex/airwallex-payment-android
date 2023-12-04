package com.airwallex.android.core.model.parser

import com.airwallex.android.core.model.Page
import com.airwallex.android.core.util.AirwallexJsonUtils
import org.json.JSONArray
import org.json.JSONObject

class PageParser<T>(private val itemParser: ModelJsonParser<T>) : ModelJsonParser<Page<T>> {
    override fun parse(json: JSONObject): Page<T> {
        val itemsJson = json.optJSONArray(FIELD_ITEMS) ?: JSONArray()
        val items = (0 until itemsJson.length())
            .map { idx -> itemsJson.optJSONObject(idx) }
            .mapNotNull {
                itemParser.parse(it)
            }

        return object : Page<T> {
            override val items: List<T>
                get() = items
            override val hasMore: Boolean
                get() = AirwallexJsonUtils.optBoolean(json, FIELD_HAS_MORE)

            override fun hashCode(): Int {
                 return json.hashCode()
            }

            override fun equals(other: Any?): Boolean {
                return this === other
            }
        }
    }

    private companion object {
        private const val FIELD_HAS_MORE = "has_more"
        private const val FIELD_ITEMS = "items"
    }
}