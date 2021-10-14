package com.airwallex.android.core.model.parser

import com.airwallex.android.core.model.PaymentMethodTypeInfoSchema
import com.airwallex.android.core.model.TransactionMode
import com.airwallex.android.core.util.AirwallexJsonUtils
import org.json.JSONArray
import org.json.JSONObject

class PaymentMethodTypeInfoSchemaParser : ModelJsonParser<PaymentMethodTypeInfoSchema> {

    private val paymentMethodTypeInfoSchemaFieldParser = PaymentMethodTypeInfoSchemaFieldParser()

    override fun parse(json: JSONObject): PaymentMethodTypeInfoSchema {
        val itemsJson = json.optJSONArray(FIELD_FIELDS) ?: JSONArray()
        val items = (0 until itemsJson.length())
            .map { idx -> itemsJson.optJSONObject(idx) }
            .mapNotNull {
                paymentMethodTypeInfoSchemaFieldParser.parse(it)
            }

        return PaymentMethodTypeInfoSchema(
            transactionMode = TransactionMode.fromValue(
                AirwallexJsonUtils.optString(json, FIELD_TRANSACTION_MODE)
            ),
            fields = items
        )
    }

    private companion object {
        private const val FIELD_TRANSACTION_MODE = "transaction_mode"
        private const val FIELD_FIELDS = "fields"
    }
}
