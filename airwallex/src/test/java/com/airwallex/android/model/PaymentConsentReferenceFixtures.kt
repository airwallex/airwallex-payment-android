package com.airwallex.android.model

import com.airwallex.android.model.parser.PaymentConsentReferenceParser
import org.json.JSONObject

internal object PaymentConsentReferenceFixtures {
    val PCR: PaymentConsentReference = PaymentConsentReferenceParser().parse(
        JSONObject(
            """
        {
            "cvc":"123",
            "id":"1"
        }
            """.trimIndent()
        )
    )
}
