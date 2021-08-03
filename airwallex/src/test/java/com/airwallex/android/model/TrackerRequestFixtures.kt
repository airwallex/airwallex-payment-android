package com.airwallex.android.model

import com.airwallex.android.model.parser.TrackerRequestParser
import org.json.JSONObject

internal object TrackerRequestFixtures {
    val TRACKERREQUEST: TrackerRequest = TrackerRequestParser().parse(
        JSONObject(
            """
        {
                "origin":"merchant",
                "complete":true,
                "empty":false,
                "application":"airwallex",
                "error":"yes",
                "intentId":"2",
                "status":"ok",
                "nextActionType":"",
                "nextActionUrl":"https://www.airwallex.com",
                "brand":"IC",
                "cardBin":"20211101",
                "path":"https://www.airwallex.com",
                "type":"card",
                "code":"onChallenge"
        }
            """.trimIndent()
        )
    )
}
