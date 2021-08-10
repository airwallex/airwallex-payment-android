package com.airwallex.android.model

import com.airwallex.android.model.parser.VerifyPaymentConsentParamsParesParser
import org.json.JSONObject

internal object VerifyPaymentConsentParamsFixtures {
    val VPCP: VerifyPaymentConsentParams = VerifyPaymentConsentParamsParesParser().parse(
        JSONObject(
            """
        {
            "clientSecret": "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            "paymentConsentId": "1",
            "amount": 0.1,
            "currency": "dollar",
            "cvc": "123",
            "paymentMethodType": "card",
            "returnUrl": "www.airwallex.com"
        }
            """.trimIndent()
        )
    )
}
