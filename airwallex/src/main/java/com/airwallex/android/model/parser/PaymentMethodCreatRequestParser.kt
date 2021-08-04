package com.airwallex.android.model.parser

import com.airwallex.android.model.*
import com.airwallex.android.model.AirwallexJsonUtils
import org.json.JSONObject

class PaymentMethodCreatRequestParser : ModelJsonParser<PaymentMethodCreateRequest> {

    override fun parse(json: JSONObject): PaymentMethodCreateRequest {
        return PaymentMethodCreateRequest(
            requestId = json.optString(FIELD_REQUEST_ID),
            customerId = json.optString(FIELD_CUSTOMER_ID),
            type = PaymentMethodType.fromValue(
                AirwallexJsonUtils.optString(json, FIELD_TYPE)
            ),
            card = json.optJSONObject(FIELD_CARD)?.let {
                PaymentMethodParser.CardParser().parse(it)
            },
            billing = json.optJSONObject(FIELD_BILLING)?.let {
                BillingParser().parse(it)
            }
        )
    }

    private companion object {
        private const val FIELD_REQUEST_ID = "request_id"
        private const val FIELD_CUSTOMER_ID = "customer_id"
        private const val FIELD_TYPE = "type"
        private const val FIELD_CARD = "card"
        private const val FIELD_BILLING = "billing"
    }
}
