package com.airwallex.android.model

import com.airwallex.android.model.parser.PaymentConsentVerifyRequestParser
import org.json.JSONObject

internal object PaymentConsentVerifyRequestFixtures {
    val PCVR: PaymentConsentVerifyRequest = PaymentConsentVerifyRequestParser().parse(
        JSONObject(
            """
        {
            "request_id":"abc",
            "return_url":"https://www.airwallex.com",
            "verificationOptions":{
                    "cardOptions":{
                        "amount":0.01,
                        "currency":"dollor",
                        "cvc":"123"
                    },
                    "thirdPartOptions":{
                        "osType":"android",
                        "flow":"in_app"
                    },
                    "type":"physical_goods"
            }
        }
            """.trimIndent()
        )
    )
}
