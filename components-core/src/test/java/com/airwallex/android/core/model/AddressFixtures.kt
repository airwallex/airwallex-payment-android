package com.airwallex.android.core.model

import com.airwallex.android.core.model.parser.AddressParser
import org.json.JSONObject

internal object AddressFixtures {
    val ADDRESS: Address = AddressParser().parse(
        JSONObject(
            """
        {
            "country_code": "US",
            "state": "CA",
            "city": "San Francisco",
            "street": "1460 Mission St.#02W101",
            "postcode": "94103"
        }
            """.trimIndent()
        )
    )
}
