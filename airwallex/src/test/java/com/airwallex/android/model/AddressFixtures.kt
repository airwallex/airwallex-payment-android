package com.airwallex.android.model

import com.airwallex.android.model.parser.AddressParser
import org.json.JSONObject

internal object AddressFixtures {
    val ADDRESS: Address = AddressParser().parse(
        JSONObject(
            """
        {
            "country_code": "CN",
            "state": "Shanghai",
            "city": "Shanghai",
            "street": "Pudong District",
            "postcode": "100000"
        }
            """.trimIndent()
        )
    )
}
