package com.airwallex.android.core.model.parser

import com.airwallex.android.core.model.AvailablePaymentMethodTypeResponse
import com.airwallex.android.core.util.AirwallexJsonUtils
import org.json.JSONArray
import org.json.JSONObject

class AvailablePaymentMethodTypeResponseParser : ModelJsonParser<AvailablePaymentMethodTypeResponse> {

    private val availablePaymentMethodParser = AvailablePaymentMethodTypeParser()

    override fun parse(json: JSONObject): AvailablePaymentMethodTypeResponse {
        val itemsJson = json.optJSONArray(FIELD_ITEMS) ?: JSONArray()
        val items = (0 until itemsJson.length())
            .map { idx -> itemsJson.optJSONObject(idx) }
            .mapNotNull {
                availablePaymentMethodParser.parse(it)
            }

        return AvailablePaymentMethodTypeResponse(
            hasMore = AirwallexJsonUtils.optBoolean(json, FIELD_HAS_MORE),
            items = items
        )
    }

    private companion object {
        private const val FIELD_HAS_MORE = "has_more"
        private const val FIELD_ITEMS = "items"
    }
}
