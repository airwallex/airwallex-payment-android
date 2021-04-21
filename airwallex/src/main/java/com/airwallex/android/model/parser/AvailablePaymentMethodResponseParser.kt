package com.airwallex.android.model.parser

import com.airwallex.android.model.AirwallexJsonUtils
import com.airwallex.android.model.AvailablePaymentMethodResponse
import org.json.JSONArray
import org.json.JSONObject

class AvailablePaymentMethodResponseParser : ModelJsonParser<AvailablePaymentMethodResponse> {

    private val availablePaymentMethodParser = AvailablePaymentMethodParser()

    override fun parse(json: JSONObject): AvailablePaymentMethodResponse {
        val itemsJson = json.optJSONArray(FIELD_ITEMS) ?: JSONArray()
        val items = (0 until itemsJson.length())
            .map { idx -> itemsJson.optJSONObject(idx) }
            .mapNotNull {
                availablePaymentMethodParser.parse(it)
            }

        return AvailablePaymentMethodResponse(
            hasMore = AirwallexJsonUtils.optBoolean(json, FIELD_HAS_MORE),
            items = items
        )
    }

    private companion object {
        private const val FIELD_HAS_MORE = "has_more"
        private const val FIELD_ITEMS = "items"
    }
}
