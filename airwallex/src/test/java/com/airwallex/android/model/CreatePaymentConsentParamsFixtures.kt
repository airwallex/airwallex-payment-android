package com.airwallex.android.model

import com.airwallex.android.model.parser.CreatePaymentConsentParamsParser
import org.json.JSONObject

internal object CreatePaymentConsentParamsFixtures {
    val createPaymentConsentParams: CreatePaymentConsentParams = CreatePaymentConsentParamsParser().parse(
        JSONObject(
            """
        {
            "clientSecret": "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            "customerId": "1",
            "paymentMethodId": "1",
            "nextTriggeredBy": "customer",
            "merchantTriggerReason": "unscheduled",
            "paymentMethodType": "card",
            "requiresCvc": true
        }
            """.trimIndent()
        )
    )
}
