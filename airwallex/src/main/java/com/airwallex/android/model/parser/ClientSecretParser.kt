package com.airwallex.android.model.parser

import com.airwallex.android.model.ClientSecret
import org.json.JSONObject

class ClientSecretParser : ModelJsonParser<ClientSecret> {

    override fun parse(json: JSONObject): ClientSecret {
        return ClientSecret(
            value = json.optString(FIELD_SECRET),
            expiredTime = requireNotNull(dateFormat.parse(json.optString(FIELD_EXPIRED_TIME)))
        )
    }

    private companion object {
        private const val FIELD_SECRET = "client_secret"
        private const val FIELD_EXPIRED_TIME = "expired_time"
    }
}
