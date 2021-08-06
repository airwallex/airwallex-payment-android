package com.airwallex.android.model.parser

import com.airwallex.android.model.AirwallexJsonUtils
import com.airwallex.android.model.WeChat
import org.json.JSONObject

class WechatParser : ModelJsonParser<WeChat> {

    override fun parse(json: JSONObject): WeChat {
        return WeChat(
            appId = AirwallexJsonUtils.optString(json, FIELD_APPID),
            partnerId = AirwallexJsonUtils.optString(json, FIELD_PARTNERID),
            prepayId = AirwallexJsonUtils.optString(json, FIELD_PREPAYID),
            `package` = AirwallexJsonUtils.optString(json, FIELD_PACKAGE),
            nonceStr = AirwallexJsonUtils.optString(json, FIELD_NONCESTR),
            sign = AirwallexJsonUtils.optString(json, FIELD_SIGN),
            timestamp = AirwallexJsonUtils.optString(json, FIELD_TIMESTAMP)
        )
    }

    companion object {
        const val FIELD_APPID = "appId"
        const val FIELD_PARTNERID = "partnerId"
        const val FIELD_PREPAYID = "prepayId"
        const val FIELD_PACKAGE = "`package`"
        const val FIELD_NONCESTR = "nonceStr"
        const val FIELD_SIGN = "sign"
        const val FIELD_TIMESTAMP = "timestamp"
    }
}
