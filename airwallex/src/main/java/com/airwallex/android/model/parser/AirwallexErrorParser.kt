package com.airwallex.android.model.parser

import com.airwallex.android.model.AirwallexError
import com.airwallex.android.model.AirwallexJsonUtils
import org.json.JSONObject

class AirwallexErrorParser : ModelJsonParser<AirwallexError> {

    override fun parse(json: JSONObject): AirwallexError {
        return AirwallexError(
            code = AirwallexJsonUtils.optString(json, FIELD_CODE),
            source = AirwallexJsonUtils.optString(json, FIELD_SOURCE),
            message = AirwallexJsonUtils.optString(json, FIELD_MESSAGE)
        )
    }

    private companion object {
        private const val FIELD_CODE = "code"
        private const val FIELD_SOURCE = "source"
        private const val FIELD_MESSAGE = "message"
    }
}
