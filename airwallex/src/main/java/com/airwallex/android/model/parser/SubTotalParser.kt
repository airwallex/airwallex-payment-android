package com.airwallex.android.model.parser

import com.airwallex.android.model.AirwallexJsonUtils
import com.airwallex.android.model.SubTotal
import org.json.JSONObject

class SubTotalParser : ModelJsonParser<SubTotal> {

    override fun parse(json: JSONObject): SubTotal {

        return SubTotal(
            firstPrice = AirwallexJsonUtils.optString(json, FIELD_FIRST_PRICE),
            secondPrice = AirwallexJsonUtils.optString(json, FIELD_SECOND_PRICE),
            firstCount = AirwallexJsonUtils.optString(json, FIELD_FIRST_COUNT),
            secondCount = AirwallexJsonUtils.optString(json, FIELD_SECOND_COUNT),
        )
    }

    companion object {
        const val FIELD_FIRST_PRICE = "firstPrice"
        const val FIELD_SECOND_PRICE = "secondPrice"
        const val FIELD_FIRST_COUNT = "firstCount"
        const val FIELD_SECOND_COUNT = "secondCount"
    }
}
