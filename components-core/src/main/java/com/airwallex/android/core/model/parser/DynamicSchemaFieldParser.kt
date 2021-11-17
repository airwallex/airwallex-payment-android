package com.airwallex.android.core.model.parser

import com.airwallex.android.core.model.*
import com.airwallex.android.core.util.AirwallexJsonUtils
import org.json.JSONObject

class DynamicSchemaFieldParser : ModelJsonParser<DynamicSchemaField> {

    private val dynamicSchemaFieldValidationParser = DynamicSchemaFieldValidationParser()

    private val dynamicSchemaFieldCandidateParser = DynamicSchemaFieldCandidateParser()

    override fun parse(json: JSONObject): DynamicSchemaField {
        val candidates = json.optJSONArray(FIELD_CANDIDATES)?.let { jsonArray ->
            (0 until jsonArray.length())
                .map { idx -> jsonArray.optJSONObject(idx) }
                .mapNotNull {
                    dynamicSchemaFieldCandidateParser.parse(it)
                }
        }

        return DynamicSchemaField(
            name = json.optString(FIELD_NAME),
            displayName = json.optString(FIELD_DISPLAY_NAME),
            uiType = AirwallexJsonUtils.optString(json, FIELD_UI_TYPE)?.let {
                DynamicSchemaFieldUIType.fromValue(it)
            },
            type = AirwallexJsonUtils.optString(json, FIELD_TYPE)?.let {
                DynamicSchemaFieldType.fromValue(it)
            },
            hidden = AirwallexJsonUtils.optBoolean(json, FIELD_HIDDEN),
            validations = json.optJSONObject(FIELD_VALIDATIONS)?.let {
                dynamicSchemaFieldValidationParser.parse(it)
            },
            candidates = candidates
        )
    }

    private companion object {
        private const val FIELD_NAME = "name"
        private const val FIELD_DISPLAY_NAME = "display_name"
        private const val FIELD_UI_TYPE = "ui_type"
        private const val FIELD_TYPE = "type"
        private const val FIELD_HIDDEN = "hidden"
        private const val FIELD_VALIDATIONS = "validations"
        private const val FIELD_CANDIDATES = "candidates"
    }
}
