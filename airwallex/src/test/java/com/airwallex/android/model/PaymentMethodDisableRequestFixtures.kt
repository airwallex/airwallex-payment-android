package com.airwallex.android.model

import com.airwallex.android.model.parser.PaymentMethodDisableRequestParser
import org.json.JSONObject

internal object PaymentMethodDisableRequestFixtures {
    val PMDR: PaymentMethodDisableRequest = PaymentMethodDisableRequestParser().parse(
        JSONObject(
            """
        {
            "request_id": "1"
        }
            """.trimIndent()
        )
    )
}
