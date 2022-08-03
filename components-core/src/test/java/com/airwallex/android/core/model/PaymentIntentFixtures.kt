package com.airwallex.android.core.model

import com.airwallex.android.core.model.parser.PaymentIntentParser
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
            "customer_payment_consents": [
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
                            "created_at":"2021-03-30T03:04:00+0000",
                            "updated_at":"2021-03-30T03:04:00+0000"
                    },
                    "initial_payment_intent_id":"",
                    "next_triggered_by":"merchant",
                    "merchant_trigger_reason":"unscheduled",
                    "status":"VERIFIED",
                    "created_at":"2021-03-30T03:04:00+0000",
                    "updated_at":"2021-03-30T03:04:00+0000",
                    "next_action":{
                        "type":"render_qr_code",
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
                "cancelled_at": "2020-03-30T03:04:00+0000",
                "authentication_data": {
                    "ds_data": {
				        "version": "2.1.0",
				        "liability_shift_indicator": "Y",
				        "eci": "05",
				        "cavv": "",
				        "xid": "TzRUOW9Eb1VXemZpMmhVa1RINTA=",
				        "enrolled": "Y",
				        "pa_res_status": "Y",
				        "challenge_cancellation_reason": "",
				        "frictionless": "N"
			        },
                    "fraud_data": {
                        "action":"VERIFY",
                        "score":"0"
                    },
                    "avs_result":"U",
                    "cvc_result":"U"
                },
                "next_action": {
                	"type": "redirect",
                	"method": "POST",
                	"url": "https://api-demo.airwallex.com/api/v1/pa/card3ds-mock/fingerprint",
                	"data": {
                		"jwt": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI2NzI4MjdiMC01YzIxLTRmMGItOGUzNS0xMmYwZjM2NThmNzIiLCJpYXQiOjE2MjkyNzYxNTUsImlzcyI6IjVlOWQ5ZmI2MTI1MzdjMzBhYzdlYjJhOCIsIk9yZ1VuaXRJZCI6IjVlOWQ5ZmI2YmUwZTg2MzQ3ZjYwNjA5YSIsIlJldHVyblVybCI6Imh0dHBzOi8vd3d3LmFpcndhbGxleC5jb20iLCJPYmplY3RpZnlQYXlsb2FkIjpmYWxzZX0.n_FvLxgLoWKe1r17b3wMcMjTybialxxuzCfpS88jxdw",
                		"stage": "WAITING_DEVICE_DATA_COLLECTION"
                	}
                }
            }
        }
            """.trimIndent()
        )
    )
}
