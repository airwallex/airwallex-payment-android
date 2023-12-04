package com.airwallex.android.core.model

import com.airwallex.android.core.model.parser.ThreeDSecureParesParser
import org.json.JSONObject

internal object ThreeDSecureParesFixtures {
    val THREEDSECUREPARES: ThreeDSecureParams = ThreeDSecureParesParser().parse(
        JSONObject(
            """
        {
                "paresId":"1",
                "pares":"render_qr_code"   
        }
            """.trimIndent()
        )
    )
}
