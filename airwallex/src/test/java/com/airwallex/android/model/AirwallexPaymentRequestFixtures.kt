package com.airwallex.android.model

import com.airwallex.android.model.parser.AirwallexPaymentRequestParser
import org.json.JSONObject

internal object AirwallexPaymentRequestFixtures {
    val REQUEST: AirwallexPaymentRequest = AirwallexPaymentRequestParser().parse(
        JSONObject(
            """
        {
            "country_code":"CN",
            "shopper_name":"cstore",
            "bank_name":"ICBC",
            "shopper_email":"cstore@163.com",
            "shopper_phone":"18833332222",
            "flow":"inapp",
            "os_type":"android"
        }
            """.trimIndent()
        )
    )
}
