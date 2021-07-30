package com.airwallex.android.model

import com.airwallex.android.model.parser.AirwallexErrorParser
import org.json.JSONObject

internal object AirwallexErrorFixtures {
    val Error: AirwallexError = AirwallexErrorParser().parse(
        JSONObject(
            """
        {
            "code": "200",
            "source": "airwallex",
            "message": "success"
        }
            """.trimIndent()
        )
    )
}
