package com.airwallex.android.core.model

import com.airwallex.android.core.model.parser.ClientSecretParser
import org.json.JSONObject

internal object ClientSecretFixtures {
    val CLIENTSECRET: ClientSecret = ClientSecretParser().parse(
        JSONObject(
            """
        {
            "client_secret":"vvXOivbXQt-bjXxFicgLgA",
            "expired_time":"2015-01-12T10:02:00+0530"
        }
            """.trimIndent()
        )
    )
}
