package com.airwallex.android.core.model

import com.airwallex.android.core.model.parser.AvailablePaymentMethodResponseParser
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
                   "flows":["IN_APP"]
                  }   
            ],
            "has_more":false
        }
            """.trimIndent()
        )
    )
}
