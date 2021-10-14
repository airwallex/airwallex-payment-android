package com.airwallex.android.core.model.parser

import com.airwallex.android.core.model.*
import com.airwallex.android.core.util.AirwallexJsonUtils
import org.json.JSONObject

class PaymentMethodTypeInfoSchemaFieldValidationParser :
    ModelJsonParser<PaymentMethodTypeInfoSchemaFieldValidation> {

    override fun parse(json: JSONObject): PaymentMethodTypeInfoSchemaFieldValidation {
        return PaymentMethodTypeInfoSchemaFieldValidation(
            regex = AirwallexJsonUtils.optString(json, FIELD_REGEX),
            max = AirwallexJsonUtils.optInt(json, FIELD_MAX)
        )
    }

    private companion object {
        private const val FIELD_REGEX = "regex"
        private const val FIELD_MAX = "max"
    }
}
