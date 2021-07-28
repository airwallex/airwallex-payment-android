package com.airwallex.android.model

import android.os.Parcelable
import com.airwallex.android.model.parser.LoginInfoParser
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * LoginInfo information.
 */
@Parcelize
data class LoginInfo internal constructor(

    /**
     * AIRWALLEX_API_KEY
     */
    val AIRWALLEX_API_KEY: String? = null,

    /**
     * AIRWALLEX_CLIENT_ID
     */
    val AIRWALLEX_CLIENT_ID: String? = null,

    /**
     * AIRWALLEX_WECHAT_APP_ID
     */
    val AIRWALLEX_WECHAT_APP_ID: String? = null,

) : AirwallexModel, AirwallexRequestModel, Parcelable {

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>()
            .plus(
                AIRWALLEX_API_KEY?.let {
                    mapOf(LoginInfoParser.FIELD_API_KEY to it)
                }.orEmpty()
            )
            .plus(
                AIRWALLEX_CLIENT_ID?.let {
                    mapOf(LoginInfoParser.FIELD_CLIENTID to it)
                }.orEmpty()
            )
            .plus(
                AIRWALLEX_WECHAT_APP_ID?.let {
                    mapOf(LoginInfoParser.FIELD_APPID to it)
                }.orEmpty()
            )

    }

    class Builder : ObjectBuilder<LoginInfo> {
        private var airwallexApiKey: String? = null
        private var airwallexClientId: String? = null
        private var airwallexWechatAppId: String? = null

        fun setApiKey(airwallexApiKey: String?): Builder = apply {
            this.airwallexApiKey = airwallexApiKey
        }

        fun setClientId(airwallexClientId: String?): Builder = apply {
            this.airwallexClientId = airwallexClientId
        }

        fun setAppId(airwallexWechatAppId: String?): Builder = apply {
            this.airwallexWechatAppId = airwallexWechatAppId
        }


        override fun build(): LoginInfo {
            return LoginInfo(
                AIRWALLEX_API_KEY = airwallexApiKey,
                AIRWALLEX_CLIENT_ID = airwallexClientId,
                AIRWALLEX_WECHAT_APP_ID = airwallexWechatAppId,
            )
        }
    }
}
