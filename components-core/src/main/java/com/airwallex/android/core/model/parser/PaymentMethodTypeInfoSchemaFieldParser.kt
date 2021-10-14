package com.airwallex.android.core.model.parser

import com.airwallex.android.core.model.*
import com.airwallex.android.core.util.AirwallexJsonUtils
import org.json.JSONObject

class PaymentMethodTypeInfoSchemaFieldParser : ModelJsonParser<PaymentMethodTypeInfoSchemaField> {

    private val paymentMethodDetailFieldValidationParser =
        PaymentMethodTypeInfoSchemaFieldValidationParser()

    override fun parse(json: JSONObject): PaymentMethodTypeInfoSchemaField {
        return PaymentMethodTypeInfoSchemaField(
            name = json.optString(FIELD_NAME),
            displayName = json.optString(FIELD_DISPLAY_NAME),
            uiType = AirwallexJsonUtils.optString(json, FIELD_UI_TYPE)?.let {
                PaymentMethodTypeInfoSchemaFieldUIType.fromValue(it)
            },
            type = AirwallexJsonUtils.optString(json, FIELD_TYPE),
            hidden = AirwallexJsonUtils.optBoolean(json, FIELD_HIDDEN),
            validations = json.optJSONObject(FIELD_VALIDATIONS)?.let {
                paymentMethodDetailFieldValidationParser.parse(it)
            }
        )
    }

    private companion object {
        private const val FIELD_NAME = "name"
        private const val FIELD_DISPLAY_NAME = "display_name"
        private const val FIELD_UI_TYPE = "ui_type"
        private const val FIELD_TYPE = "type"
        private const val FIELD_HIDDEN = "hidden"
        private const val FIELD_VALIDATIONS = "validations"
    }
}
