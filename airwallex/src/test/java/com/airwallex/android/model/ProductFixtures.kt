package com.airwallex.android.model

import com.airwallex.android.model.parser.PhysicalProductParser
import org.json.JSONObject

internal object ProductFixtures {
    val PRODUCT: PhysicalProduct = PhysicalProductParser().parse(JSONObject(
        """
        {
            "code": "123",
            "name": "AirPods Pro",
            "desc": "Buy AirPods Pro, per month with trade-in",
            "sku": "piece",
            "type": "Free engraving",
            "unit_price": 399.00,
            "url": "www.aircross.com",
            "quantity": 1
        }
        """.trimIndent()
    ))
}
