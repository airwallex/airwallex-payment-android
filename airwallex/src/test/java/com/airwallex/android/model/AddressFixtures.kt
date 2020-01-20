package com.airwallex.android.model

import com.airwallex.android.AirwallexPlugins

internal object AddressFixtures {
    val ADDRESS: Address = AirwallexPlugins.gson.fromJson(
        """
        {
            "country_code": "CN",
            "state": "Shanghai",
            "city": "Shanghai",
            "street": "Pudong District",
            "postcode": "100000"
        }
        """.trimIndent(),
        Address::class.java
    )
}
