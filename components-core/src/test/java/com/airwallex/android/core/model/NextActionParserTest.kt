package com.airwallex.android.core.model

import com.airwallex.android.core.model.parser.NextActionParser
import org.json.JSONObject
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class NextActionParserTest {

    @Test
    fun testParseWithAllFields() {
        val nextAction = NextActionParser().parse(
            JSONObject(
                """
                {
                    "stage": "WAITING_USER_INFO_INPUT",
                    "type": "redirect_form",
                    "data": {
                        "key1": "value1",
                        "key2": "value2"
                    },
                    "dcc_data": {
                        "currency": "USD",
                        "amount": 100.50,
                        "currency_pair": "USD/EUR",
                        "client_rate": 0.85,
                        "rate_source": "test_source",
                        "rate_timestamp": "2025-12-31T23:59:59Z",
                        "rate_expiry": "2025-12-31T23:59:59Z"
                    },
                    "url": "https://example.com/redirect",
                    "method": "POST",
                    "package_name": "com.example.app",
                    "fallback_url": "https://example.com/fallback"
                }
                """.trimIndent()
            )
        )

        assertEquals(NextAction.NextActionStage.WAITING_USER_INFO_INPUT, nextAction.stage)
        assertEquals(NextAction.NextActionType.REDIRECT_FORM, nextAction.type)
        assertNotNull(nextAction.data)
        assertEquals("value1", nextAction.data?.get("key1"))
        assertNotNull(nextAction.dcc)
        assertEquals("USD", nextAction.dcc?.currency)
        assertEquals(BigDecimal.valueOf(100.50), nextAction.dcc?.amount)
        assertEquals("https://example.com/redirect", nextAction.url)
        assertEquals("POST", nextAction.method)
        assertEquals("com.example.app", nextAction.packageName)
        assertEquals("https://example.com/fallback", nextAction.fallbackUrl)
    }

    @Test
    fun testParseWithDccDataPresent() {
        // Line 19: Test ?.let true branch when dcc_data is present
        val nextAction = NextActionParser().parse(
            JSONObject(
                """
                {
                    "stage": "WAITING_USER_INFO_INPUT",
                    "type": "redirect_form",
                    "dcc_data": {
                        "currency": "EUR",
                        "amount": 50.00,
                        "client_rate": 0.90
                    }
                }
                """.trimIndent()
            )
        )

        assertNotNull(nextAction.dcc)
        assertEquals("EUR", nextAction.dcc?.currency)
        assertEquals(BigDecimal.valueOf(50.00), nextAction.dcc?.amount)
        assertEquals(0.90, nextAction.dcc?.clientRate)
    }

    @Test
    fun testParseWithDccDataNull() {
        // Line 19: Test ?.let false branch when dcc_data is missing
        val nextAction = NextActionParser().parse(
            JSONObject(
                """
                {
                    "stage": "WAITING_USER_INFO_INPUT",
                    "type": "redirect_form",
                    "url": "https://example.com/redirect"
                }
                """.trimIndent()
            )
        )

        assertNull(nextAction.dcc)
        assertEquals("https://example.com/redirect", nextAction.url)
    }

    @Test
    fun testParseDccDataWithAmountPresent() {
        // DccDataParser line 45: Test ?.let true branch when amount is present
        val nextAction = NextActionParser().parse(
            JSONObject(
                """
                {
                    "stage": "WAITING_USER_INFO_INPUT",
                    "type": "redirect_form",
                    "dcc_data": {
                        "currency": "GBP",
                        "amount": 75.25,
                        "currency_pair": "GBP/USD"
                    }
                }
                """.trimIndent()
            )
        )

        assertNotNull(nextAction.dcc)
        assertNotNull(nextAction.dcc?.amount)
        assertEquals(BigDecimal.valueOf(75.25), nextAction.dcc?.amount)
    }

    @Test
    fun testParseDccDataWithAmountNull() {
        // DccDataParser line 45: Test ?.let false branch when amount is missing
        val nextAction = NextActionParser().parse(
            JSONObject(
                """
                {
                    "stage": "WAITING_USER_INFO_INPUT",
                    "type": "redirect_form",
                    "dcc_data": {
                        "currency": "JPY",
                        "client_rate": 110.50
                    }
                }
                """.trimIndent()
            )
        )

        assertNotNull(nextAction.dcc)
        assertEquals("JPY", nextAction.dcc?.currency)
        assertNull(nextAction.dcc?.amount)
        assertEquals(110.50, nextAction.dcc?.clientRate)
    }

    @Test
    fun testParseWithMinimalFields() {
        val nextAction = NextActionParser().parse(
            JSONObject(
                """
                {
                    "stage": "WAITING_USER_INFO_INPUT",
                    "type": "call_sdk"
                }
                """.trimIndent()
            )
        )

        assertEquals(NextAction.NextActionStage.WAITING_USER_INFO_INPUT, nextAction.stage)
        assertEquals(NextAction.NextActionType.CALL_SDK, nextAction.type)
        assertNull(nextAction.data)
        assertNull(nextAction.dcc)
        assertNull(nextAction.url)
        assertNull(nextAction.method)
        assertNull(nextAction.packageName)
        assertNull(nextAction.fallbackUrl)
    }

    @Test
    fun testParseWithEmptyDccData() {
        val nextAction = NextActionParser().parse(
            JSONObject(
                """
                {
                    "stage": "WAITING_USER_INFO_INPUT",
                    "type": "redirect_form",
                    "dcc_data": {}
                }
                """.trimIndent()
            )
        )

        assertNotNull(nextAction.dcc)
        assertNull(nextAction.dcc?.currency)
        assertNull(nextAction.dcc?.amount)
        assertNull(nextAction.dcc?.currencyPair)
        assertNull(nextAction.dcc?.clientRate)
    }
}
