package com.airwallex.android.model

import com.airwallex.android.AirwallexPlugins

internal object PaymentMethodFixtures {
    val PAYMENT_METHOD: PaymentMethod = AirwallexPlugins.gson.fromJson(
        """
        {
            "id": "",
            "request_id": "",
            "type": "card",
	        "card": {
	            "number": "4012000300001003",
	            "expiry_month": "12",
	            "expiry_year": "2020",  
	            "cvc": "123",
	            "name": "Adam"
	        },
            "billing": {
                "date_of_birth": "2011-10-12",
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
        """.trimIndent(),
        PaymentMethod::class.java
    )
}
