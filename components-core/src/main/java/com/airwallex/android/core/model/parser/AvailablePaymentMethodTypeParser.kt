package com.airwallex.android.core.model.parser

import com.airwallex.android.core.model.AirwallexPaymentRequestFlow
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.TransactionMode
import com.airwallex.android.core.util.AirwallexJsonUtils
import org.json.JSONObject

class AvailablePaymentMethodTypeParser : ModelJsonParser<AvailablePaymentMethodType> {

    private val availablePaymentMethodResourceParser = AvailablePaymentMethodTypeResourceParser()

    override fun parse(json: JSONObject): AvailablePaymentMethodType {

        return AvailablePaymentMethodType(
            name = json.optString(FIELD_NAME),
            displayName = AirwallexJsonUtils.optString(json, FIELD_DISPLAY_NAME),
            transactionMode = TransactionMode.fromValue(
                AirwallexJsonUtils.optString(json, FIELD_TRANSACTION_MODE)
            ),
            flows = AirwallexJsonUtils.jsonArrayToList(json.optJSONArray(FIELD_FLOWS))
                .orEmpty()
                .map { AirwallexPaymentRequestFlow.valueOf(it.toString()) },
            transactionCurrencies = ModelJsonParser.jsonArrayToList(
                json.optJSONArray(FIELD_TRANSACTION_CURRENCIES)
            ),
            countryCodes = ModelJsonParser.jsonArrayToList(json.optJSONArray(FIELD_COUNTRY_CODES)),
            active = AirwallexJsonUtils.optBoolean(json, FIELD_ACTIVE),
            resources = json.optJSONObject(FIELD_RESOURCES)?.let {
                availablePaymentMethodResourceParser.parse(it)
            }
        )
    }

    companion object {
        const val FIELD_NAME = "name"
        const val FIELD_DISPLAY_NAME = "display_name"
        const val FIELD_TRANSACTION_MODE = "transaction_mode"
        const val FIELD_FLOWS = "flows"
        const val FIELD_TRANSACTION_CURRENCIES = "transaction_currencies"
        const val FIELD_COUNTRY_CODES = "country_codes"
        const val FIELD_ACTIVE = "active"
        const val FIELD_RESOURCES = "resources"
    }
}
