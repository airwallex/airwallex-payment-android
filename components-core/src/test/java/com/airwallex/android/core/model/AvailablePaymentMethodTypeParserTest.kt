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

    @Test
    fun testParseWithNullFlows() {
        // Line 22: Test .orEmpty() branch when flows is null
        val availablePaymentMethodType = AvailablePaymentMethodTypeParser().parse(
            JSONObject(
                """
    {
        "name":"card",
        "display_name":"display",
        "transaction_mode":"oneoff",
        "active":true
    }
                """.trimIndent()
            )
        )
        assertEquals(availablePaymentMethodType.name, "card")
        assertEquals(availablePaymentMethodType.flows, emptyList())
    }

    @Test
    fun testParseWithNullResources() {
        // Line 30: Test ?.let null branch when resources is missing
        val availablePaymentMethodType = AvailablePaymentMethodTypeParser().parse(
            JSONObject(
                """
    {
        "name":"card",
        "display_name":"display",
        "transaction_mode":"oneoff",
        "active":true
    }
                """.trimIndent()
            )
        )
        assertEquals(availablePaymentMethodType.name, "card")
        assertEquals(availablePaymentMethodType.resources, null)
    }

    @Test
    fun testParseWithNullCardSchemes() {
        // Line 33: Test ?.let null branch when card_schemes is missing
        val availablePaymentMethodType = AvailablePaymentMethodTypeParser().parse(
            JSONObject(
                """
    {
        "name":"card",
        "display_name":"display",
        "transaction_mode":"oneoff",
        "active":true
    }
                """.trimIndent()
            )
        )
        assertEquals(availablePaymentMethodType.name, "card")
        assertEquals(availablePaymentMethodType.cardSchemes, null)
    }

    @Test
    fun testParseWithCardSchemeMissingName() {
        // Line 36: Test when card scheme name is missing (optString returns empty string)
        val availablePaymentMethodType = AvailablePaymentMethodTypeParser().parse(
            JSONObject(
                """
    {
        "name":"card",
        "display_name":"display",
        "transaction_mode":"oneoff",
        "active":true,
        "card_schemes": [
            {
                "display_name": "Mastercard"
            }
        ]
    }
                """.trimIndent()
            )
        )
        assertEquals(availablePaymentMethodType.name, "card")
        // When name field is missing, optString returns empty string, so CardScheme is still created
        assertEquals(availablePaymentMethodType.cardSchemes?.size, 1)
        assertEquals(availablePaymentMethodType.cardSchemes?.firstOrNull()?.name, "")
    }

    @Test
    fun testParseWithCardSchemeNullObject() {
        // Line 36: Test when cardSchemes array contains null
        val availablePaymentMethodType = AvailablePaymentMethodTypeParser().parse(
            JSONObject(
                """
    {
        "name":"card",
        "display_name":"display",
        "transaction_mode":"oneoff",
        "active":true,
        "card_schemes": [
            null,
            {
                "name": "visa"
            }
        ]
    }
                """.trimIndent()
            )
        )
        assertEquals(availablePaymentMethodType.name, "card")
        assertEquals(availablePaymentMethodType.cardSchemes?.size, 1)
        assertEquals(availablePaymentMethodType.cardSchemes?.firstOrNull()?.name, "visa")
    }

    @Test
    fun testParseWithEmptyFlows() {
        // Line 22: Test .orEmpty() branch with empty flows array
        val availablePaymentMethodType = AvailablePaymentMethodTypeParser().parse(
            JSONObject(
                """
    {
        "name":"card",
        "display_name":"display",
        "transaction_mode":"oneoff",
        "active":true,
        "flows": []
    }
                """.trimIndent()
            )
        )
        assertEquals(availablePaymentMethodType.name, "card")
        assertEquals(availablePaymentMethodType.flows, emptyList())
    }

    @Test
    fun testParseWithResourcesButNoLogos() {
        // AvailablePaymentMethodTypeResourceParser line 13: Test ?.let null branch when logos is missing
        val availablePaymentMethodType = AvailablePaymentMethodTypeParser().parse(
            JSONObject(
                """
    {
        "name":"card",
        "display_name":"display",
        "transaction_mode":"oneoff",
        "active":true,
        "resources": {
            "has_schema": true
        }
    }
                """.trimIndent()
            )
        )
        assertEquals(availablePaymentMethodType.name, "card")
        assertEquals(availablePaymentMethodType.resources?.hasSchema, true)
        assertEquals(availablePaymentMethodType.resources?.logos, null)
    }

}
