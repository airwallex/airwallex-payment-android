package com.airwallex.android.core.model.parser

import com.airwallex.android.core.model.ThreeDSecureParams
import org.json.JSONObject

class ThreeDSecureParesParser : ModelJsonParser<ThreeDSecureParams> {

    override fun parse(json: JSONObject): ThreeDSecureParams {
        return ThreeDSecureParams(
            paresId = json.optString(FIELD_PARES_ID),
            pares = json.optString(FIELD_PARES)
        )
    }

    private companion object {
        private const val FIELD_PARES_ID = "paresId"
        private const val FIELD_PARES = "pares"
    }
}
