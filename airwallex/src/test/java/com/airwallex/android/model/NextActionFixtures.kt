package com.airwallex.android.model

import com.airwallex.android.model.parser.NextActionParser
import org.json.JSONObject

internal object NextActionFixtures {
    val NEXTACTION: NextAction? = NextActionParser().parse(
        JSONObject(
            """
        {

                "type":"render_qr_code",
                "dcc_data":{
                    "currency":"1",
                    "amount":0.1,
                    "currency_pair":"1",
                    "client_rate":6.44,
                    "rate_source":"financialMarket",
                    "rate_timestamp":"1627881115",
                    "rate_expiry":"1"
                },
                "url":"https://www.airwallex.com",
                "method":"post"
            
        }
            """.trimIndent()
        )
    )
}
