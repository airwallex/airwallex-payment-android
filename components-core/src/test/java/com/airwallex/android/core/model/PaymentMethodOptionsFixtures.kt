package com.airwallex.android.core.model

import com.airwallex.android.core.model.parser.PaymentMethodOptionsParser
import org.json.JSONObject

internal object PaymentMethodOptionsFixtures {
    val PAYMENTMETHODOPTIONS: PaymentMethodOptions? = PaymentMethodOptionsParser().parse(
        JSONObject(
            """
        {
            "card": {
                "auto_capture": true,
                "three_ds": {
                    "return_url":"https://www.360safe.com",
                    "device_data_collection_res":"abc",
                    "ds_transaction_id":"123"
                }
            }
        }
            """.trimIndent()
        )
    )
}
