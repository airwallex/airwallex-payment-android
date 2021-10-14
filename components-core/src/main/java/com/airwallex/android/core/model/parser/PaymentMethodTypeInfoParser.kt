package com.airwallex.android.core.model.parser

import com.airwallex.android.core.model.PaymentMethodTypeInfo
import com.airwallex.android.core.util.AirwallexJsonUtils
import org.json.JSONArray
import org.json.JSONObject

class PaymentMethodTypeInfoParser : ModelJsonParser<PaymentMethodTypeInfo> {

    private val paymentMethodTypeInfoSchemaParser = PaymentMethodTypeInfoSchemaParser()

    override fun parse(json: JSONObject): PaymentMethodTypeInfo {
        val itemsJson = json.optJSONArray(FIELD_FIELD_SCHEMAS) ?: JSONArray()
        val items = (0 until itemsJson.length())
            .map { idx -> itemsJson.optJSONObject(idx) }
            .mapNotNull {
                paymentMethodTypeInfoSchemaParser.parse(it)
            }

        return PaymentMethodTypeInfo(
            name = AirwallexJsonUtils.optString(json, FIELD_NAME),
            displayName = AirwallexJsonUtils.optString(json, FIELD_DISPLAY_NAME),
            logos = json.optJSONObject(FIELD_LOGO_URL)?.let {
                LogoResourcesParser().parse(it)
            },
            hasSchema = AirwallexJsonUtils.optBoolean(json, FIELD_HAS_SCHEMA),
            fieldSchemas = items
        )
    }

    private companion object {
        private const val FIELD_NAME = "name"
        private const val FIELD_DISPLAY_NAME = "display_name"
        private const val FIELD_LOGO_URL = "logos"
        private const val FIELD_HAS_SCHEMA = "has_schema"
        private const val FIELD_FIELD_SCHEMAS = "field_schemas"
    }
}
