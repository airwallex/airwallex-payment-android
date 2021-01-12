package com.airwallex.android.model.parser

import com.airwallex.android.model.*
import com.airwallex.android.model.AirwallexJsonUtils
import org.json.JSONObject

class AliPayRequestParser : ModelJsonParser<AliPayRequest> {

    override fun parse(json: JSONObject): AliPayRequest? {
        return AliPayRequest(
            flow = ThirdPartPayRequestFlow.fromValue(
                AirwallexJsonUtils.optString(json, FIELD_FLOW)
            ),
            osType = AirwallexJsonUtils.optString(json, FIELD_OS_TYPE)
        )
    }

    companion object {
        const val FIELD_FLOW = "flow"
        const val FIELD_OS_TYPE = "os_type"
    }
}
