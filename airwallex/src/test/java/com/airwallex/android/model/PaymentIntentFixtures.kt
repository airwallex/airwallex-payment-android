package com.airwallex.android.model

import com.airwallex.android.AirwallexPlugins

internal object PaymentIntentFixtures {
    val PAYMENT_INTENT: PaymentIntent = AirwallexPlugins.gson.fromJson(
        """
        {
            "id": "int_6hJ72Y7zich939UCz8j6BLkonH",
            "request_id": "a750e597-c30e-4d2b-ad41-cac601a15b25",
            "amount": 100.01,
            "currency": "USD",
            "merchant_order_id": "cc9bfc13-ba30-483b-a62c-ee9250c9bfev",
            "order": {
                "type": "physical_goods"
            },
            "customer_id": "cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu",
            "descriptor": "Airwallex - T-shirt",
            "status": "REQUIRES_PAYMENT_METHOD",
            "captured_amount": 0,
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
                    },
                    "created_at": "2020-02-13T06:13:05+0000",
                    "updated_at": "2020-02-13T06:13:05+0000"
                }
            ],
            "client_secret": "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ=="
        }
        """.trimIndent(),
        PaymentIntent::class.java
    )
}
