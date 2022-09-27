package com.airwallex.android.core.model

import com.airwallex.android.core.model.parser.AvailablePaymentMethodTypeParser
import org.json.JSONObject
import org.junit.Test
import kotlin.test.assertEquals

class AvailablePaymentMethodTypeParserTest {
    @Test
    fun testParseResult() {
        val availablePaymentMethodType = AvailablePaymentMethodTypeParser().parse(
            JSONObject(
                """
    {
        "name":"card",
        "display_name":"display",
        "transaction_mode":"oneoff",
        "active":true,
        "transaction_currencies":["dollar","RMB"],
        "country_codes":["CN","AU"],
        "flows":["inapp", "mweb"],
        "resources": {
        "has_schema": false,
        "logos": {
          "png": "https://checkout-staging.airwallex.com/static/media/cardPlaceholder.abcb5ee7.png",
          "svg": "https://checkout-staging.airwallex.com/static/media/cardPlaceholder.abcb5ee7.svg"
        }
        },
      "card_schemes": [
        {
          "name": "mastercard",
          "display_name": "Mastercard",
          "resources": {
            "logos": {
              "png": "https://checkout-staging.airwallex.com/static/media/mastercard.823272.png",
              "svg": "https://checkout-staging.airwallex.com/static/media/mastercard.262f85fc.svg"
            }
          }
        }
       ]
    },
        }
                """.trimIndent()
            )
        )
        assertEquals(availablePaymentMethodType.name, "card")
        assertEquals(availablePaymentMethodType.displayName, "display")
        assertEquals(availablePaymentMethodType.transactionMode, TransactionMode.ONE_OFF)
        assertEquals(availablePaymentMethodType.active, true)
        assertEquals(availablePaymentMethodType.transactionCurrencies, listOf("dollar", "RMB"))
        assertEquals(availablePaymentMethodType.countryCodes, listOf("CN", "AU"))
        assertEquals(
            availablePaymentMethodType.flows,
            listOf(AirwallexPaymentRequestFlow.IN_APP, AirwallexPaymentRequestFlow.M_WEB)
        )
        assertEquals(availablePaymentMethodType.resources?.hasSchema, false)
        assertEquals(
            availablePaymentMethodType.resources?.logos?.png,
            "https://checkout-staging.airwallex.com/static/media/cardPlaceholder.abcb5ee7.png"
        )
        assertEquals(
            availablePaymentMethodType.resources?.logos?.svg,
            "https://checkout-staging.airwallex.com/static/media/cardPlaceholder.abcb5ee7.svg"
        )
        assertEquals(availablePaymentMethodType.cardSchemes?.firstOrNull()?.name, "mastercard")
    }
}
