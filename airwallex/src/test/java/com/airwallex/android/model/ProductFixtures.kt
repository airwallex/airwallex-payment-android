package com.airwallex.android.model

import com.airwallex.android.AirwallexPlugins

internal object ProductFixtures {
    val PRODUCT: PhysicalProduct = AirwallexPlugins.gson.fromJson(
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
        """.trimIndent(),
        PhysicalProduct::class.java
    )
}
