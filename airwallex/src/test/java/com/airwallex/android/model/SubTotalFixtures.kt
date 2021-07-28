package com.airwallex.android.model

import com.airwallex.android.model.parser.ShippingParser
import com.airwallex.android.model.parser.SubTotalParser
import org.json.JSONObject

internal object SubTotalFixtures {
    val SUBTOTAL = SubTotalParser().parse(
        JSONObject(
            """
        {
            "firstPrice": "399",
            "secondPrice": "469",
            "firstCount": "1",
            "secondCount": "1"
        }
            """.trimIndent()
        )
    )
}
