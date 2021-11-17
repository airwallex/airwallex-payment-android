package com.airwallex.android.core.model.parser

import com.airwallex.android.core.model.*
import org.json.JSONObject

class DynamicSchemaFieldCandidateParser :
    ModelJsonParser<DynamicSchemaFieldCandidate> {

    override fun parse(json: JSONObject): DynamicSchemaFieldCandidate {
        return DynamicSchemaFieldCandidate(
            displayName = json.optString(FIELD_DISPLAY_NAME),
            value = json.optString(FIELD_VALUE)
        )
    }

    private companion object {
        private const val FIELD_DISPLAY_NAME = "display_name"
        private const val FIELD_VALUE = "value"
    }
}
