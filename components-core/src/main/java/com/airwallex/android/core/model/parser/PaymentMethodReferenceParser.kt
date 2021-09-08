package com.airwallex.android.core.model.parser

import com.airwallex.android.core.model.PaymentMethodReference
import org.json.JSONObject

class PaymentMethodReferenceParser : ModelJsonParser<PaymentMethodReference> {

    override fun parse(json: JSONObject): PaymentMethodReference? {
        return PaymentMethodReference(
            id = json.optString(FIELD_ID),
            cvc = json.optString(FIELD_CVC)
        )
    }

    companion object {
        const val FIELD_ID = "id"
        const val FIELD_CVC = "cvc"
    }
}
