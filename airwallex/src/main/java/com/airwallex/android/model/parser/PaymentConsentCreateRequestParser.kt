package com.airwallex.android.model.parser

import com.airwallex.android.model.*
import com.airwallex.android.model.AirwallexJsonUtils
import org.json.JSONObject

class PaymentConsentCreateRequestParser : ModelJsonParser<PaymentConsentCreateRequest> {

    override fun parse(json: JSONObject): PaymentConsentCreateRequest {
        return PaymentConsentCreateRequest(
            requestId = AirwallexJsonUtils.optString(json, FIELD_REQUEST_ID),
            customerId = AirwallexJsonUtils.optString(json, FIELD_CUSTOMER_ID),
            paymentMethodRequest = json.optJSONObject(FIELD_PAYMENT_METHOD)?.let {
                PaymentMethodRequestParser().parse(it)
            },
            nextTriggeredBy = PaymentConsent.NextTriggeredBy.fromValue(
                AirwallexJsonUtils.optString(json, FIELD_NEXT_TRIGGERED_BY)
            ),
            requiresCvc = AirwallexJsonUtils.optBoolean(json, FIELD_REQUIRES_CVC)
        )
    }

    private companion object {
        private const val FIELD_REQUEST_ID = "request_id"
        private const val FIELD_CUSTOMER_ID = "customer_id"
        private const val FIELD_PAYMENT_METHOD = "payment_method"
        private const val FIELD_NEXT_TRIGGERED_BY = "next_triggered_by"
        private const val FIELD_REQUIRES_CVC = "requires_cvc"
    }
}
