package com.airwallex.android.model.parser

import com.airwallex.android.model.*
import org.json.JSONObject

class PaymentMethodDisableRequestParser : ModelJsonParser<PaymentMethodDisableRequest> {

    override fun parse(json: JSONObject): PaymentMethodDisableRequest {
        return PaymentMethodDisableRequest(
            requestId = json.optString(FIELD_REQUEST_ID)
        )
    }

    private companion object {
        private const val FIELD_REQUEST_ID = "request_id"
    }
}
