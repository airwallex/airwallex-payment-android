package com.airwallex.android.model

import com.airwallex.android.model.parser.PaymentConsentDisableRequestParser
import org.json.JSONObject

internal object PaymentConsentDisableRequestFixtures {
    val PCDR: PaymentConsentDisableRequest = PaymentConsentDisableRequestParser().parse(
        JSONObject(
            """
        {
            "request_id":"abc"
        }
            """.trimIndent()
        )
    )
}
