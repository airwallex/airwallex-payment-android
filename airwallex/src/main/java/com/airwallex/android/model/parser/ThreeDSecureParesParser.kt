package com.airwallex.android.model.parser

import com.airwallex.android.model.ThreeDSecurePares
import org.json.JSONObject

class ThreeDSecureParesParser : ModelJsonParser<ThreeDSecurePares> {

    override fun parse(json: JSONObject): ThreeDSecurePares {
        return ThreeDSecurePares(
            paresId = json.optString(FIELD_PARES_ID),
            pares = json.optString(FIELD_PARES)
        )
    }

    private companion object {
        private const val FIELD_PARES_ID = "paresId"
        private const val FIELD_PARES = "pares"
    }
}
