package com.airwallex.android.model

import com.airwallex.android.model.parser.PurchaseOrderParser
import org.json.JSONObject

internal object PurchaseOrderFixtures {
    val PURCHASEORDER: PurchaseOrder = PurchaseOrderParser().parse(
        JSONObject(
            """
        {
            "shipping":{
                "first_name":"John",
                "last_name":"Doe",
                "phone_number":"13800000000",
                "address":{
                    "country_code":"CN",
                    "state":"Shanghai",
                    "city":"Shanghai",
                    "street":"Pudong District",
                    "postcode":"100000"
                },
                "shipping_method":"logistics"
            },
            "products":[
                            {
                             "code":"123",
                             "name":"AirPods Pro",
                             "desc":"Buy AirPods Pro, per month with trade-in",
                             "sku":"piece",
                             "type":"Free engraving",
                             "unit_price":399.00,
                             "url":"www.aircross.com",
                             "quantity":1
                             }
                        ],
            "type":"card"
        }
            """.trimIndent()
        )
    )
}
