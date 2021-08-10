package com.airwallex.android.core.model.parser

import com.airwallex.android.core.model.Address
import com.airwallex.android.core.util.AirwallexJsonUtils
import org.json.JSONObject

class AddressParser : ModelJsonParser<Address> {

    override fun parse(json: JSONObject): Address {
        return Address(
            countryCode = AirwallexJsonUtils.optString(json, FIELD_COUNTRY_CODE),
            state = AirwallexJsonUtils.optString(json, FIELD_STATE),
            city = AirwallexJsonUtils.optString(json, FIELD_CITY),
            street = AirwallexJsonUtils.optString(json, FIELD_STREET),
            postcode = AirwallexJsonUtils.optString(json, FIELD_POSTCODE)
        )
    }

    companion object {
        const val FIELD_COUNTRY_CODE = "country_code"
        const val FIELD_STATE = "state"
        const val FIELD_CITY = "city"
        const val FIELD_STREET = "street"
        const val FIELD_POSTCODE = "postcode"
    }
}
