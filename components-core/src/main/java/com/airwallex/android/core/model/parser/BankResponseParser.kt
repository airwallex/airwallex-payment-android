package com.airwallex.android.core.model.parser

import com.airwallex.android.core.model.BankResponse
import com.airwallex.android.core.util.AirwallexJsonUtils
import org.json.JSONArray
import org.json.JSONObject

class BankResponseParser : ModelJsonParser<BankResponse> {

    private val bankParser = BankParser()

    override fun parse(json: JSONObject): BankResponse {
        val itemsJson = json.optJSONArray(FIELD_ITEMS) ?: JSONArray()
        val items = (0 until itemsJson.length())
            .map { idx -> itemsJson.optJSONObject(idx) }
            .mapNotNull {
                bankParser.parse(it)
            }

        return BankResponse(
            hasMore = AirwallexJsonUtils.optBoolean(json, FIELD_HAS_MORE),
            items = items
        )
    }

    companion object {
        private const val FIELD_HAS_MORE = "has_more"
        private const val FIELD_ITEMS = "items"
    }
}
