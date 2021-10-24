package com.airwallex.android.core.model.parser

import com.airwallex.android.core.model.DynamicSchema
import com.airwallex.android.core.model.TransactionMode
import com.airwallex.android.core.util.AirwallexJsonUtils
import org.json.JSONArray
import org.json.JSONObject

class DynamicSchemaParser : ModelJsonParser<DynamicSchema> {

    private val dynamicSchemaFieldParser = DynamicSchemaFieldParser()

    override fun parse(json: JSONObject): DynamicSchema {
        val itemsJson = json.optJSONArray(FIELD_FIELDS) ?: JSONArray()
        val items = (0 until itemsJson.length())
            .map { idx -> itemsJson.optJSONObject(idx) }
            .mapNotNull {
                dynamicSchemaFieldParser.parse(it)
            }

        return DynamicSchema(
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
