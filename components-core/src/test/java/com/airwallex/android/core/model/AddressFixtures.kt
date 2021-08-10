package com.airwallex.android.core.model

import com.airwallex.android.core.model.parser.AddressParser
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
