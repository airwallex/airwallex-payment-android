package com.airwallex.android.core.model

import com.airwallex.android.core.model.parser.AirwallexPaymentRequestParser
import org.json.JSONObject

internal object AirwallexPaymentRequestFixtures {
    val REQUEST: AirwallexPaymentRequest = AirwallexPaymentRequestParser().parse(
        JSONObject(
            """
        {
            "country_code":"CN",
            "shopper_name":"cstore",
            "bank_name":"krungsri",
            "shopper_email":"cstore@163.com",
            "shopper_phone":"18833332222",
            "flow":"inapp",
            "os_type":"android"
        }
            """.trimIndent()
        )
    )
}
