package com.airwallex.android.model

import com.airwallex.android.model.parser.PaymentMethodCreatRequestParser
import org.json.JSONObject

internal object PaymentMethodCreatRequestFixtures {
    val PMMCR: PaymentMethodCreateRequest = PaymentMethodCreatRequestParser().parse(
        JSONObject(
            """
        {
            "request_id": "1",
            "customer_id": "2",
            "type":"card",
            "card": {
	            "number": "4012000300001003",
	            "expiry_month": "12",
	            "expiry_year": "2020",  
	            "cvc": "123",
	            "name": "Adam"
	        },
            "billing": {
                "first_name": "Jim",
                "last_name": "He",
                "phone_number": "1367875786",
                "address": {
                    "country_code": "CN",
                    "state": "Shanghai",
                    "city": "Shanghai",
                    "street": "Pudong District",
                    "postcode": "201304"
                }
            }
        }
            """.trimIndent()
        )
    )
}
