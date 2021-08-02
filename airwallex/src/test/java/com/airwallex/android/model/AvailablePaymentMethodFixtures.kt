package com.airwallex.android.model

import com.airwallex.android.model.parser.AvailablePaymentMethodParser
import org.json.JSONObject

internal object AvailablePaymentMethodFixtures {
    val PAYMENMETHOD: AvailablePaymentMethod = AvailablePaymentMethodParser().parse(
        JSONObject(
            """
        {
            "name":"card",
            "transaction_mode":"oneoff",
            "active":true,
            "transaction_currencies":["dollar","RMB"],
            "flows":"in_app"
        }
            """.trimIndent()
        )
    )
}
