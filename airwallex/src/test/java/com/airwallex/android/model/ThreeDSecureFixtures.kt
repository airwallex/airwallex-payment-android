package com.airwallex.android.model

import com.airwallex.android.model.parser.ThreeDSecureParser
import org.json.JSONObject

internal object ThreeDSecureFixtures {
    val THREEDSECURE: ThreeDSecure? = ThreeDSecureParser().parse(
        JSONObject(
            """
        {
            "return_url": "https://www.airwallex.com",
            "device_data_collection_res": "abc",
            "ds_transaction_id": "123"
        }
            """.trimIndent()
        )
    )
}
