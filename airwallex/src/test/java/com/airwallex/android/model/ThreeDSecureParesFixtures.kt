package com.airwallex.android.model

import com.airwallex.android.model.parser.ThreeDSecureParesParser
import org.json.JSONObject

internal object ThreeDSecureParesFixtures {
    val THREEDSECUREPARES: ThreeDSecurePares = ThreeDSecureParesParser().parse(
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
