package com.airwallex.android.model.parser

import com.airwallex.android.model.AirwallexJsonUtils
import com.airwallex.android.model.Billing
import org.json.JSONObject

class BillingParser : ModelJsonParser<Billing> {

    override fun parse(json: JSONObject): Billing {
        val address = json.optJSONObject(FIELD_ADDRESS)?.let {
            AddressParser().parse(it)
        }

        return Billing(
            firstName = AirwallexJsonUtils.optString(json, FIELD_FIRST_NAME),
            lastName = AirwallexJsonUtils.optString(json, FIELD_LAST_NAME),
            phone = AirwallexJsonUtils.optString(json, FIELD_PHONE),
            email = AirwallexJsonUtils.optString(json, FIELD_EMAIL),
            dateOfBirth = AirwallexJsonUtils.optString(json, FIELD_DATE_OF_BIRTH),
            address = address
        )
    }

    companion object {
        const val FIELD_FIRST_NAME = "first_name"
        const val FIELD_LAST_NAME = "last_name"
        const val FIELD_PHONE = "phone_number"
        const val FIELD_EMAIL = "email"
        const val FIELD_DATE_OF_BIRTH = "date_of_birth"
        const val FIELD_ADDRESS = "address"
    }
}
