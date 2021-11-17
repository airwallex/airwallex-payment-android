package com.airwallex.android.core.model.parser

import com.airwallex.android.core.model.Bank
import org.json.JSONObject

class BankParser : ModelJsonParser<Bank> {

    override fun parse(json: JSONObject): Bank {
        return Bank(
            name = json.optString(FIELD_BANK_NAME),
            displayName = json.optString(FIELD_DISPLAY_NAME),
            resources = json.optJSONObject(FIELD_RESOURCES)?.let {
                BankResourcesParser().parse(it)
            }
        )
    }

    companion object {
        const val FIELD_BANK_NAME = "bank_name"
        const val FIELD_DISPLAY_NAME = "display_name"
        const val FIELD_RESOURCES = "resources"
    }
}
