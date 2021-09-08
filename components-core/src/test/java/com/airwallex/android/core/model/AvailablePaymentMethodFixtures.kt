package com.airwallex.android.core.model

import com.airwallex.android.core.model.parser.AvailablePaymentMethodParser
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
            "flows":["IN_APP"]
        }
            """.trimIndent()
        )
    )
}
