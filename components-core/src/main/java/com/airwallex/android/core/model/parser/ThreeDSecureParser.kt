package com.airwallex.android.core.model.parser

import com.airwallex.android.core.model.ThreeDSecure
import com.airwallex.android.core.util.AirwallexJsonUtils
import org.json.JSONObject

class ThreeDSecureParser : ModelJsonParser<ThreeDSecure> {

    override fun parse(json: JSONObject): ThreeDSecure {
        return ThreeDSecure(
            returnUrl = AirwallexJsonUtils.optString(json, FIELD_RETURN_URL),
            acsResponse = AirwallexJsonUtils.optString(json, FIELD_ACS_RESPONSE)
        )
    }

    companion object {
        const val FIELD_RETURN_URL = "return_url"
        const val FIELD_ACS_RESPONSE = "acs_response"
    }
}
