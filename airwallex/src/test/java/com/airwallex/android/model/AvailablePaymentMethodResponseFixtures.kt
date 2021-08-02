package com.airwallex.android.model

import com.airwallex.android.model.parser.AvailablePaymentMethodResponseParser
import org.json.JSONObject

internal object AvailablePaymentMethodResponseFixtures {
    val PAYMENMETHODRESPONSE: AvailablePaymentMethodResponse = AvailablePaymentMethodResponseParser().parse(
        JSONObject(
            """
        {
        "items":[ 
                  {
                   "name":"card",
                   "transaction_mode":"oneoff",
                   "active":true,
                   "transaction_currencies":["dollar","RMB"],
                   "flows":"in_app"
                  }   
            ],
            "has_more":false
        }
            """.trimIndent()
        )
    )
}
