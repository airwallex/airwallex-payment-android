package com.airwallex.android.model

import com.airwallex.android.model.parser.PaymentIntentParser
import org.json.JSONObject

internal object PaymentIntentFixtures {
    val PAYMENT_INTENT: PaymentIntent = PaymentIntentParser().parse(
        JSONObject(
            """
        {
            "id": "int_6hJ72Y7zich939UCz8j6BLkonH",
            "request_id": "a750e597-c30e-4d2b-ad41-cac601a15b25",
            "amount": 100.01,
            "currency": "AUD",
            "merchant_order_id": "cc9bfc13-ba30-483b-a62c-ee9250c9bfev",
            "order": {
                "type": "physical_goods"
            },
            "customer_id": "cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu",
            "descriptor": "Airwallex - T-shirt",
            "status": "REQUIRES_PAYMENT_METHOD",
            "captured_amount": 0.1,
            "available_payment_method_types": [
                "card",
                "wechatpay"
            ],
            "customer_payment_methods": [
                {
                    "id": "",
                    "request_id": "",
                    "type": "card",
                    "card": {
                        "expiry_month": "12",
                        "expiry_year": "2030",
                        "name": "John Doe",
                        "bin": "411111",
                        "last4": "1111",
                        "brand": "visa",
                        "issuer_country_code": "US",
                        "card_type": "credit",
                        "fingerprint": "7e9cceb282d05675fed72f67e0a4a5ae4e82ff5a96a1b0e55bc45cf63609a055"
                    },
                    "billing": {
                        "first_name": "John",
                        "last_name": "Doe",
                        "email": "john.doe@airwallex.com",
                        "phone_number": "13800000000",
                        "address": {
                            "country_code": "CN",
                            "state": "Shanghai",
                            "city": "Shanghai",
                            "street": "Pudong District",
                            "postcode": "100000"
                        }
                    }
                }
            ],
            "client_secret": "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            "created_at": "2020-03-30T03:03:37+0000",
            "updated_at": "2020-03-30T03:04:02+0000",
            "latest_payment_attempt": {
                "id": "att_7P9rxcJzs06b3Bt7zLWArVk3xi",
                "amount": 100.01,
                "payment_method": {
                    "id": "mtd_4iyImkz7wglVXRad6hZWreqRJY0",
                    "type": "card",
                    "card": {
                        "expiry_month": "01",
                        "expiry_year": "2023",
                        "name": "Adam",
                        "bin": "520000",
                        "last4": "1005",
                        "brand": "mastercard",
                        "issuer_country_code": "MY",
                        "card_type": "credit",
                        "fingerprint": "290a1f394301fa8bd83be3f081a5d308d7f9fd89dd72c7c4108029dec75f72ae",
                        "cvc_check": "unknown",
                        "avs_check": "unknown"
                    },
                    "billing": {
                        "first_name": "Jim",
                        "last_name": "passlist",
                        "email": "jim631@sina.com",
                        "phone_number": "1367875788",
                        "date_of_birth": "2011-10-12",
                        "address": {
                            "country_code": "CN",
                            "state": "Beijing",
                            "city": "Shanghai",
                            "street": "Pudong District",
                            "postcode": "33333"
                        }
                    },
                    "status": "VERIFIED",
                    "created_at": "2020-03-30T03:04:00+0000",
                    "updated_at": "2020-03-30T03:04:00+0000"
                },
                "status": "SUCCEEDED",
                "captured_amount": 0.1,
                "refunded_amount": 0.1,
                "created_at": "2020-03-30T03:04:00+0000",
                "updated_at": "2020-03-30T03:04:00+0000",
                "amount": 0.1,
                "authentication_data": {}
            }
        }
            """.trimIndent()
        )
    )
}
