package com.airwallex.android.model

import com.airwallex.android.model.parser.PaymentMethodDisableRequestParser
import org.json.JSONObject

internal object PaymentConsentCreateRequestFixtures {
    val PCCR: PaymentMethodDisableRequest = PaymentMethodDisableRequestParser().parse(
        JSONObject(
            """
        {
            "request_id":"abc",
            "customer_id":"1",
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
                        "fingerprint":"290a1f394301fa8bd83be3f081a5d308d7f9fd89dd72c7c4108029dec75f72ae",
                        "cvc_check":"unknown",
                        "avs_check":"unknown"
                    },
                    "billing":{
                        "first_name":"Jim",
                        "last_name":"passlist",
                        "email":"jim631@sina.com",
                        "phone_number":"1367875788",
                        "date_of_birth":"2011-10-12",
                        "address": {
                            "country_code":"CN",
                            "state":"Beijing",
                            "city":"Shanghai",
                            "street":"Pudong District",
                            "postcode":"33333"
                        }
                    },
                    "status":"VERIFIED",
                    "created_at":"2020-03-30T03:04:00+0000",
                    "updated_at":"2020-03-30T03:04:00+0000"
            },
            "next_triggered_by":"merchant",
            "merchant_trigger_reason":"test"
        }
            """.trimIndent()
        )
    )
}
