package com.airwallex.android.model

import com.airwallex.android.model.parser.PaymentConsentParser
import org.json.JSONObject

internal object PaymentConsentFixtures {
    val PAYMENTCONSENT: PaymentConsent = PaymentConsentParser().parse(
        JSONObject(
            """
        {
            "id":"123",
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
            "initial_payment_intent_id":"",
            "next_triggered_by":"merchant",
            "merchant_trigger_reason":"test",
            "status":"success",
            "created_at":"2021-03-30T03:04:00+0000",
            "updated_at":"2021-03-30T03:04:00+0000",
            "next_action":{
                "type":"",
                "dcc_data":{
                    "currency":"1",
                    "amount":0.1,
                    "currency_pair":"1",
                    "client_rate":6.44,
                    "rate_source":"financialMarket",
                    "rate_timestamp":"1627881115",
                    "rate_expiry":"1"
                },
                "url":"https://www.airwallex.com",
                "method":"post"
            },
            client_secret:"faqCACD3TAaeVjaltPa-Ig"
        }
            """.trimIndent()
        )
    )
}
