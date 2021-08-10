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
            "address": {
                "country_code": "CN",
                "state": "Shanghai",
                "city": "Shanghai",
                "street": "Pudong District",
                "postcode": "100000"
            }
        }
            """.trimIndent()
        )
    )
}
