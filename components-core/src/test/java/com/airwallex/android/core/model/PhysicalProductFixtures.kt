package com.airwallex.android.core.model

import com.airwallex.android.core.model.parser.PhysicalProductParser
import org.json.JSONObject

object PhysicalProductFixtures {

    val PHYSICAL_PRODUCT: PhysicalProduct = PhysicalProductParser().parse(
        JSONObject(
            """
        {
            "type": "White",
            "code": "123",
            "name": "AirPods",
	        "sku": "piece",
            "quantity": 1,
            "unit_price": 500.0,
            "desc": "Buy AirPods Pro, per month with trade-in",
            "url": "www.aircross.com"
        }
        """.trimIndent()
        )
    )
}
