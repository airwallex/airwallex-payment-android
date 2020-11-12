package com.airwallex.android.model.parser

import com.airwallex.android.model.PaymentMethodResponse
import org.json.JSONArray
import org.json.JSONObject

class PaymentMethodResponseParser : ModelJsonParser<PaymentMethodResponse> {

    private val paymentMethodParser = PaymentMethodParser()

    override fun parse(json: JSONObject): PaymentMethodResponse {
        val itemsJson = json.optJSONArray(FIELD_ITEMS) ?: JSONArray()
        val items = (0 until itemsJson.length())
            .map { idx -> itemsJson.optJSONObject(idx) }
            .mapNotNull {
                paymentMethodParser.parse(it)
            }

        return PaymentMethodResponse(
            hasMore = json.optBoolean(FIELD_HAS_MORE),
            items = items
        )
    }

    private companion object {
        private const val FIELD_HAS_MORE = "has_more"
        private const val FIELD_ITEMS = "items"
    }
}
