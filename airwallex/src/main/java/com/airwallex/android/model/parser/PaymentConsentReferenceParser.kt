package com.airwallex.android.model.parser

import com.airwallex.android.model.*
import com.airwallex.android.model.AirwallexJsonUtils
import org.json.JSONObject

class PaymentConsentReferenceParser : ModelJsonParser<PaymentConsentReference> {

    override fun parse(json: JSONObject): PaymentConsentReference {
        return PaymentConsentReference(
            id = AirwallexJsonUtils.optString(json, FIELD_ID),
            cvc = AirwallexJsonUtils.optString(json, FIELD_CVC)
        )
    }

    private companion object {
        private const val FIELD_ID = "id"
        private const val FIELD_CVC = "cvc"
    }
}
