package com.airwallex.android.model

import com.airwallex.android.model.parser.ThreeDSecureLookupParser
import org.json.JSONObject

internal object ThreeDSecureLookupFixtures {
    val THREEDSECURELOOKUP: ThreeDSecureLookup = ThreeDSecureLookupParser().parse(
        JSONObject(
            """
        {
                "transactionId":"1",
                "payload":"application/json",
                "acsUrl":"https://www.airwallex.com",
                "version":"2"
        }
            """.trimIndent()
        )
    )
}
