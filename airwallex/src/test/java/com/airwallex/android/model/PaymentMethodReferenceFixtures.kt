package com.airwallex.android.model

import com.airwallex.android.model.parser.PaymentMethodReferenceParser
import org.json.JSONObject

internal object PaymentMethodReferenceFixtures {
    val PAYMENTMETHODREFERRENCE: PaymentMethodReference? = PaymentMethodReferenceParser().parse(
        JSONObject(
            """
        {
            "id":"1",
            "cvc":"123"
        }
            """.trimIndent()
        )
    )
}
