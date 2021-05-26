package com.airwallex.android.model.parser

import com.airwallex.android.model.*
import com.airwallex.android.model.AirwallexJsonUtils
import org.json.JSONObject

class ThirdPartPayRequestParser : ModelJsonParser<ThirdPartPayRequest> {

    override fun parse(json: JSONObject): ThirdPartPayRequest {
        return ThirdPartPayRequest(
            flow = AirwallexJsonUtils.optString(json, FIELD_FLOW)?.let {
                ThirdPartPayRequestFlow.fromValue(it)
            },
            osType = AirwallexJsonUtils.optString(json, FIELD_OS_TYPE)
        )
    }

    companion object {
        const val FIELD_FLOW = "flow"
        const val FIELD_OS_TYPE = "os_type"
    }
}
