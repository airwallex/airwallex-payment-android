package com.airwallex.android.core.model.parser

import com.airwallex.android.core.model.BankResources
import org.json.JSONObject

class BankResourcesParser : ModelJsonParser<BankResources> {

    override fun parse(json: JSONObject): BankResources {
        return BankResources(
            logos = json.optJSONObject(FIELD_LOGO_URL)?.let {
                LogoResourcesParser().parse(it)
            }
        )
    }

    private companion object {
        private const val FIELD_LOGO_URL = "logos"
    }
}
