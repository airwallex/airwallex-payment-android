package com.airwallex.android.model.parser

import com.airwallex.android.model.AirwallexJsonUtils
import com.airwallex.android.model.WeChatPayRequest
import com.airwallex.android.model.WeChatPayRequestFlow
import org.json.JSONObject

class WeChatPayRequestParser : ModelJsonParser<WeChatPayRequest> {

    override fun parse(json: JSONObject): WeChatPayRequest? {
        return WeChatPayRequest(
            flow = WeChatPayRequestFlow.fromValue(
                AirwallexJsonUtils.optString(json, FIELD_FLOW)
            )
        )
    }

    companion object {
        const val FIELD_FLOW = "flow"
    }
}
