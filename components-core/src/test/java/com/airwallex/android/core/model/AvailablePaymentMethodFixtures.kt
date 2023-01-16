package com.airwallex.android.core.model

import com.airwallex.android.core.model.parser.AvailablePaymentMethodTypeParser
import org.json.JSONObject

internal object AvailablePaymentMethodFixtures {
    val PAYMENT_METHOD: AvailablePaymentMethodType = AvailablePaymentMethodTypeParser().parse(
        JSONObject(
            """
        {
            "name":"card",
            "transaction_mode":"oneoff",
            "active":true,
            "transaction_currencies":["dollar","RMB"],
            "flows":["inapp"]
        }
            """.trimIndent()
        )
    )
}
