package com.airwallex.android.core.model

import com.airwallex.android.core.model.parser.AirwallexErrorParser
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
