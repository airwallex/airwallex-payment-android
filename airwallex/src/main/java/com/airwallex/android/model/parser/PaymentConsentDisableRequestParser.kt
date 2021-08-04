package com.airwallex.android.model.parser

import com.airwallex.android.model.*
import com.airwallex.android.model.AirwallexJsonUtils
import org.json.JSONObject

class PaymentConsentDisableRequestParser : ModelJsonParser<PaymentConsentDisableRequest> {

    override fun parse(json: JSONObject): PaymentConsentDisableRequest {
        return PaymentConsentDisableRequest(
            requestId = AirwallexJsonUtils.optString(json, FIELD_REQUEST_ID)
        )
    }

    private companion object {
        private const val FIELD_REQUEST_ID = "request_id"
    }
}
