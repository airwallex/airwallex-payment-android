package com.airwallex.android.model.parser

import com.airwallex.android.model.*
import com.airwallex.android.model.AirwallexJsonUtils
import org.json.JSONObject

class AirwallexPaymentRequestParser : ModelJsonParser<AirwallexPaymentRequest> {

    override fun parse(json: JSONObject): AirwallexPaymentRequest {
        return AirwallexPaymentRequest(
            bank = AirwallexJsonUtils.optString(json, BANK_NAME)?.let {
                Bank.fromValue(it)
            },
            name = AirwallexJsonUtils.optString(json, SHOPPER_NAME),
            email = AirwallexJsonUtils.optString(json, SHOPPER_EMAIL),
            phone = AirwallexJsonUtils.optString(json, SHOPPER_PHONE),
            countryCode = AirwallexJsonUtils.optString(json, COUNTRY_CODE),
            flow = AirwallexJsonUtils.optString(json, FIELD_FLOW)?.let {
                AirwallexPaymentRequestFlow.fromValue(it)
            },
            osType = AirwallexJsonUtils.optString(json, FIELD_OS_TYPE)
        )
    }

    companion object {
        const val COUNTRY_CODE = "country_code"
        const val SHOPPER_NAME = "shopper_name"
        const val BANK_NAME = "bank_name"
        const val SHOPPER_EMAIL = "shopper_email"
        const val SHOPPER_PHONE = "shopper_phone"
        const val FIELD_FLOW = "flow"
        const val FIELD_OS_TYPE = "os_type"
    }
}
