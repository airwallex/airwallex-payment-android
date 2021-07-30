package com.airwallex.android.model

import com.airwallex.android.model.parser.AddressParser
import com.airwallex.android.model.parser.AirwallexErrorParser
import org.json.JSONObject
import java.lang.Error

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
