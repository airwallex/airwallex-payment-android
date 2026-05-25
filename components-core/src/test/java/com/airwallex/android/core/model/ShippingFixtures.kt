package com.airwallex.android.core.model

import com.airwallex.android.core.model.parser.ShippingParser
import org.json.JSONObject

internal object ShippingFixtures {
    val SHIPPING = ShippingParser().parse(
        JSONObject(
            """
        {
            "first_name": "John",
            "last_name": "Doe",
            "phone_number": "13800000000",
            "email": "john.doe@airwallex.com",
            "shipping_method": "shipping",
            "address": {
                "country_code": "US",
                "state": "CA",
                "city": "San Francisco",
                "street": "9999 Mission St.",
                "postcode": "94103"
            }
        }
            """.trimIndent()
        )
    )
}
