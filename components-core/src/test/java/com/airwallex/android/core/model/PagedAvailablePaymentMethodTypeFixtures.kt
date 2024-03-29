package com.airwallex.android.core.model

import com.airwallex.android.core.model.parser.AvailablePaymentMethodTypeParser
import com.airwallex.android.core.model.parser.PageParser
import org.json.JSONObject

internal object PagedAvailablePaymentMethodTypeFixtures {
    val PAYMENMETHODRESPONSE = PageParser(
        AvailablePaymentMethodTypeParser()
    ).parse(
        JSONObject(
            """
        {
        "items":[ 
                  {
                   "name":"card",
                   "transaction_mode":"oneoff",
                   "active":true,
                   "transaction_currencies":["dollar","RMB"],
                   "flows":["inapp"]
                  }   
            ],
            "has_more":false
        }
            """.trimIndent()
        )
    )
}
