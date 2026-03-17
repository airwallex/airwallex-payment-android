package com.airwallex.android.core.model.parser

import org.json.JSONObject
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DynamicSchemaFieldValidationParserTest {

    private val parser = DynamicSchemaFieldValidationParser()

    @Test
    fun `test parse with all fields`() {
        val json = JSONObject(
            """
            {
                "regex": "^[0-9]+$",
                "max": 100
            }
            """.trimIndent()
        )

        val validation = parser.parse(json)

        assertEquals("^[0-9]+$", validation.regex)
        assertEquals(100, validation.max)
    }

    @Test
    fun `test parse with only regex`() {
        val json = JSONObject(
            """
            {
                "regex": "^[a-z]+$"
            }
            """.trimIndent()
        )

        val validation = parser.parse(json)

        assertEquals("^[a-z]+$", validation.regex)
        assertNull(validation.max)
    }

    @Test
    fun `test parse with only max`() {
        val json = JSONObject(
            """
            {
                "max": 50
            }
            """.trimIndent()
        )

        val validation = parser.parse(json)

        assertNull(validation.regex)
        assertEquals(50, validation.max)
    }

    @Test
    fun `test parse with empty json`() {
        val json = JSONObject("{}")

        val validation = parser.parse(json)

        assertNull(validation.regex)
        assertNull(validation.max)
    }
}
