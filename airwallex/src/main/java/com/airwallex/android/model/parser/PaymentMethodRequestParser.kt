package com.airwallex.android.model.parser

import com.airwallex.android.model.*
import com.airwallex.android.model.AirwallexJsonUtils
import org.json.JSONObject

class PaymentMethodRequestParser : ModelJsonParser<PaymentMethodRequest> {

    override fun parse(json: JSONObject): PaymentMethodRequest {
        return PaymentMethodRequest(
            id = json.optString(FIELD_ID),
            type = PaymentMethodType.fromValue(
                AirwallexJsonUtils.optString(json, FIELD_TYPE)
            )!!,
            card = json.optJSONObject(FIELD_CARD)?.let {
                PaymentMethodParser.CardParser().parse(it)
            },
            billing = json.optJSONObject(FIELD_BILLING)?.let {
                BillingParser().parse(it)
            }
        )
    }

    private companion object {
        private const val FIELD_ID = "id"
        private const val FIELD_TYPE = "type"
        private const val FIELD_CARD = "card"
        private const val FIELD_BILLING = "billing"
    }
}
