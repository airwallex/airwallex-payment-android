package com.airwallex.android.model.parser

import com.airwallex.android.model.AirwallexJsonUtils
import com.airwallex.android.model.LoginInfo
import org.json.JSONObject

class LoginInfoParser : ModelJsonParser<LoginInfo> {

    override fun parse(json: JSONObject): LoginInfo {
        return LoginInfo(
            AIRWALLEX_API_KEY = AirwallexJsonUtils.optString(json, FIELD_API_KEY),
            AIRWALLEX_CLIENT_ID = AirwallexJsonUtils.optString(json, FIELD_CLIENTID),
            AIRWALLEX_WECHAT_APP_ID = AirwallexJsonUtils.optString(json, FIELD_APPID),
        )
    }

    companion object {
        const val FIELD_API_KEY = "AIRWALLEX_API_KEY"
        const val FIELD_CLIENTID = "AIRWALLEX_CLIENT_ID"
        const val FIELD_APPID = "AIRWALLEX_WECHAT_APP_ID"
    }
}
