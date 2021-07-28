package com.airwallex.android.model

import com.airwallex.android.model.parser.AddressParser
import com.airwallex.android.model.parser.LoginInfoParser
import org.json.JSONObject

internal object  LoginFixtures {
    val loginInfo:LoginInfo = LoginInfoParser().parse(
        JSONObject(
            """
        { "AIRWALLEX_API_KEY":"3fb8c8095d2ecee1451d62d0abfe74a21a8e3324afed60dc5e2ca27370f3b9a540716bb7078fdc5b020581d5e97fbafc",
          "AIRWALLEX_CLIENT_ID":"vvXOivbXQt-bjXxFicgLgA",
          "AIRWALLEX_WECHAT_APP_ID":"wxfad13fd6681a62b0"
        }
            """.trimIndent()
        )
    )
}