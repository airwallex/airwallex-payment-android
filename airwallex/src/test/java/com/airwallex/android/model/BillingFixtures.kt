package com.airwallex.android.model

import com.airwallex.android.model.parser.BillingParser
import org.json.JSONObject

internal object BillingFixtures {
    val BILLING: Billing = BillingParser().parse(JSONObject(
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
        """.trimIndent()
    ))
}
