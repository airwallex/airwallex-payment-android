package com.airwallex.android.model.parser

import com.airwallex.android.model.AirwallexJsonUtils
import com.airwallex.android.model.AvailablePaymentMethod
import org.json.JSONObject

class AvailablePaymentMethodParser : ModelJsonParser<AvailablePaymentMethod> {

    override fun parse(json: JSONObject): AvailablePaymentMethod {

        return AvailablePaymentMethod(
            name = AirwallexJsonUtils.optString(json, FIELD_NAME),
            transactionMode = AvailablePaymentMethod.TransactionMode.fromValue(AirwallexJsonUtils.optString(json, FIELD_TRANSACTION_MODE)),
            flows = ModelJsonParser.jsonArrayToList(json.optJSONArray(FIELD_FLOWS)),
            transactionCurrencies = ModelJsonParser.jsonArrayToList(json.optJSONArray(FIELD_TRANSACTION_CURRENCIES)),
            active = AirwallexJsonUtils.optBoolean(json, FIELD_ACTIVE)
        )
    }

    companion object {
        const val FIELD_ACTIVE = "active"
        const val FIELD_FLOWS = "flows"
        const val FIELD_NAME = "name"
        const val FIELD_TRANSACTION_CURRENCIES = "transaction_currencies"
        const val FIELD_TRANSACTION_MODE = "transaction_mode"
    }
}
