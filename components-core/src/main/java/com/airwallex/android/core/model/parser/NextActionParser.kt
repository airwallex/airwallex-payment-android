package com.airwallex.android.core.model.parser

import com.airwallex.android.core.model.NextAction
import com.airwallex.android.core.util.AirwallexJsonUtils
import org.json.JSONObject
import java.math.BigDecimal

class NextActionParser : ModelJsonParser<NextAction> {

    override fun parse(json: JSONObject): NextAction {
        return NextAction(
            stage = NextAction.NextActionStage.fromValue(
                AirwallexJsonUtils.optString(json, FIELD_TYPE)
            ),
            type = NextAction.NextActionType.fromValue(
                AirwallexJsonUtils.optString(json, FIELD_TYPE)
            ),
            data = AirwallexJsonUtils.optMap(json, FIELD_DATA),
            dcc = json.optJSONObject(FIELD_DCC_DATA)?.let {
                DccDataParser().parse(it)
            },
            url = AirwallexJsonUtils.optString(json, FIELD_URL),
            method = AirwallexJsonUtils.optString(json, FIELD_METHOD),
        )
    }

    private companion object {
        private const val FIELD_TYPE = "type"
        private const val FIELD_DATA = "data"
        private const val FIELD_DCC_DATA = "dcc_data"
        private const val FIELD_URL = "url"
        private const val FIELD_METHOD = "method"
    }

    internal class DccDataParser : ModelJsonParser<NextAction.DccData> {

        override fun parse(json: JSONObject): NextAction.DccData {
            return NextAction.DccData(
                currency = AirwallexJsonUtils.optString(json, FIELD_CURRENCY),
                amount = AirwallexJsonUtils.optDouble(json, FIELD_AMOUNT)?.let {
                    BigDecimal.valueOf(it)
                },
                currencyPair = AirwallexJsonUtils.optString(json, FIELD_CURRENCY_PAIR),
                clientRate = AirwallexJsonUtils.optDouble(json, FIELD_CLIENT_RATE),
                rateSource = AirwallexJsonUtils.optString(json, FIELD_RATE_SOURCE),
                rateTimestamp = AirwallexJsonUtils.optString(json, FIELD_RATE_TIMESTAMP),
                rateExpiry = AirwallexJsonUtils.optString(json, FIELD_RATE_EXPIRY)
            )
        }

        private companion object {
            private const val FIELD_CURRENCY = "currency"
            private const val FIELD_AMOUNT = "amount"
            private const val FIELD_CURRENCY_PAIR = "currency_pair"
            private const val FIELD_CLIENT_RATE = "client_rate"
            private const val FIELD_RATE_SOURCE = "rate_source"
            private const val FIELD_RATE_TIMESTAMP = "rate_timestamp"
            private const val FIELD_RATE_EXPIRY = "rate_expiry"
        }
    }
}
