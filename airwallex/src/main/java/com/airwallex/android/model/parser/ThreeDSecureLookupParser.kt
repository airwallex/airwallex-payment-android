package com.airwallex.android.model.parser

import com.airwallex.android.model.ThreeDSecureLookup
import org.json.JSONObject

class ThreeDSecureLookupParser : ModelJsonParser<ThreeDSecureLookup> {

    override fun parse(json: JSONObject): ThreeDSecureLookup {
        return ThreeDSecureLookup(
            transactionId = json.optString(FIELD_TRANSACTION_ID),
            payload = json.optString(FIELD_PAYLOAD),
            acsUrl = json.optString(FIELD_ACSURL),
            version = json.optString(FIELD_VERSION)
        )
    }

    private companion object {
        private const val FIELD_TRANSACTION_ID = "transactionId"
        private const val FIELD_PAYLOAD = "payload"
        private const val FIELD_ACSURL = "acsUrl"
        private const val FIELD_VERSION = "version"
    }
}
