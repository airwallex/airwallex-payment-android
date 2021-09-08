package com.airwallex.android.core.model.parser

import com.airwallex.android.core.model.Shipping
import com.airwallex.android.core.util.AirwallexJsonUtils
import org.json.JSONObject

class ShippingParser : ModelJsonParser<Shipping> {

    override fun parse(json: JSONObject): Shipping {
        val address = json.optJSONObject(FIELD_ADDRESS)?.let {
            AddressParser().parse(it)
        }

        return Shipping(
            firstName = AirwallexJsonUtils.optString(json, FIELD_FIRST_NAME),
            lastName = AirwallexJsonUtils.optString(json, FIELD_LAST_NAME),
            phoneNumber = AirwallexJsonUtils.optString(json, FIELD_PHONE_NUMBER),
            email = AirwallexJsonUtils.optString(json, FIELD_EMAIL),
            shippingMethod = AirwallexJsonUtils.optString(json, FIELD_SHIPPING_METHOD),
            address = address
        )
    }

    companion object {
        const val FIELD_FIRST_NAME = "first_name"
        const val FIELD_LAST_NAME = "last_name"
        const val FIELD_PHONE_NUMBER = "phone_number"
        const val FIELD_EMAIL = "email"
        const val FIELD_SHIPPING_METHOD = "shipping_method"
        const val FIELD_ADDRESS = "address"
    }
}
