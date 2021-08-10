package com.airwallex.android.model

import com.airwallex.android.model.parser.ConfirmPaymentIntentParamsParser
import org.json.JSONObject

internal object ConfirmPaymentIntentParamsFixtures {
    val CPIP: ConfirmPaymentIntentParams = ConfirmPaymentIntentParamsParser().parse(
        JSONObject(
            """
        {
            "paymentIntentId":"abc",
            "customer_id":"1",
            "clientSecret": "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            "payment_method":{
                "id":"mtd_4iyImkz7wglVXRad6hZWreqRJY0",
                    "type":"card",
                    "card":{
                        "expiry_month":"01",
                        "expiry_year":"2023",
                        "name":"Adam",
                        "bin":"520000",
                        "last4":"1005",
                        "brand":"mastercard",
                        "issuer_country_code":"MY",
                        "card_type":"credit",
                        "fingerprint":"290a1f394301fa8bd83be3f081a5d308d7f9fd89dd72c7c4108029dec75f72ae"
                    },
                    "billing":{
                        "first_name":"Jim",
                        "last_name":"passlist",
                        "email":"jim631@sina.com",
                        "phone_number":"1367875788",
                        "address": {
                            "country_code":"CN",
                            "state":"Beijing",
                            "city":"Shanghai",
                            "street":"Pudong District",
                            "postcode":"33333"
                        }
                    }
            },
            "cvc":"123",
            "paymentMethodType":"wechatpay",
            "currency":"dollor",
            "paymentConsentId":"1"
        }
            """.trimIndent()
        )
    )
}
