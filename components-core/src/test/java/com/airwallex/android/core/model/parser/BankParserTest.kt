package com.airwallex.android.core.model.parser

import org.json.JSONObject
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class BankParserTest {

    private val parser = BankParser()

    @Test
    fun `test parse bank with all fields`() {
        val json = JSONObject(
            """
            {
                "bank_name": "test_bank",
                "display_name": "Test Bank",
                "resources": {
                    "logos": {
                        "png": "https://example.com/logo.png",
                        "svg": "https://example.com/logo.svg"
                    }
                }
            }
            """.trimIndent()
        )

        val bank = parser.parse(json)

        assertEquals("test_bank", bank.name)
        assertEquals("Test Bank", bank.displayName)
        assertNotNull(bank.resources)
        assertNotNull(bank.resources?.logos)
        assertEquals("https://example.com/logo.png", bank.resources?.logos?.png)
        assertEquals("https://example.com/logo.svg", bank.resources?.logos?.svg)
    }

    @Test
    fun `test parse bank without resources`() {
        val json = JSONObject(
            """
            {
                "bank_name": "simple_bank",
                "display_name": "Simple Bank"
            }
            """.trimIndent()
        )

        val bank = parser.parse(json)

        assertEquals("simple_bank", bank.name)
        assertEquals("Simple Bank", bank.displayName)
        assertNull(bank.resources)
    }

    @Test
    fun `test parse bank with resources but no logos`() {
        val json = JSONObject(
            """
            {
                "bank_name": "test_bank",
                "display_name": "Test Bank",
                "resources": {}
            }
            """.trimIndent()
        )

        val bank = parser.parse(json)

        assertEquals("test_bank", bank.name)
        assertEquals("Test Bank", bank.displayName)
        assertNotNull(bank.resources)
        assertNull(bank.resources?.logos)
    }
}
