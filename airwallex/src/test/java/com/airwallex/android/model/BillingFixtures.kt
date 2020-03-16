package com.airwallex.android.model

import com.airwallex.android.AirwallexPlugins

internal object BillingFixtures {
    val BILLING: Billing = AirwallexPlugins.gson.fromJson(
        """
        {
            "first_name": "John",
            "last_name": "Doe",
            "email": "john.doe@airwallex.com",
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
        Billing::class.java
    )
}
