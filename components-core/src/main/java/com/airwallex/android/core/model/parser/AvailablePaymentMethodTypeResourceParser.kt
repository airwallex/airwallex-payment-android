package com.airwallex.android.core.model.parser

import com.airwallex.android.core.model.AvailablePaymentMethodTypeResource
import com.airwallex.android.core.util.AirwallexJsonUtils
import org.json.JSONObject

class AvailablePaymentMethodTypeResourceParser :
    ModelJsonParser<AvailablePaymentMethodTypeResource> {

    override fun parse(json: JSONObject): AvailablePaymentMethodTypeResource {
        return AvailablePaymentMethodTypeResource(
            hasSchema = AirwallexJsonUtils.optBoolean(json, FIELD_HAS_SCHEMA),
            logos = json.optJSONObject(FIELD_LOGO_URL)?.let {
                LogoResourcesParser().parse(it)
            }
        )
    }

    private companion object {
        private const val FIELD_HAS_SCHEMA = "has_schema"
        private const val FIELD_LOGO_URL = "logos"
    }
}
