package com.airwallex.android.model

import com.airwallex.android.AirwallexPlugins

internal object ShippingFixtures {
    val SHIPPING: Shipping = AirwallexPlugins.gson.fromJson(
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
        """.trimIndent(),
        Shipping::class.java
    )
}
