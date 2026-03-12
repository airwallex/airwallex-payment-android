package com.airwallex.android.core.model.parser

import com.airwallex.android.core.model.DynamicSchemaFieldType
import com.airwallex.android.core.model.DynamicSchemaFieldUIType
import org.json.JSONObject
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DynamicSchemaFieldParserTest {

    private val parser = DynamicSchemaFieldParser()

    @Test
    fun `test parse with all fields`() {
        val json = JSONObject(
            """
            {
                "name": "phone_number",
                "display_name": "Phone Number",
                "ui_type": "phone",
                "type": "string",
                "hidden": true,
                "validations": {
                    "regex": "^[0-9]{10}$",
                    "max": 10
                },
                "candidates": [
                    {
                        "value": "option1",
                        "display_name": "Option 1"
                    }
                ]
            }
            """.trimIndent()
        )

        val field = parser.parse(json)

        assertEquals("phone_number", field.name)
        assertEquals("Phone Number", field.displayName)
        assertEquals(DynamicSchemaFieldUIType.PHONE, field.uiType)
        assertEquals(DynamicSchemaFieldType.STRING, field.type)
        assertTrue(field.hidden)
        assertNotNull(field.validations)
        assertEquals("^[0-9]{10}$", field.validations?.regex)
        assertEquals(10, field.validations?.max)
        assertNotNull(field.candidates)
        assertEquals(1, field.candidates?.size)
    }

    @Test
    fun `test parse without optional fields`() {
        val json = JSONObject(
            """
            {
                "name": "simple_field",
                "display_name": "Simple Field"
            }
            """.trimIndent()
        )

        val field = parser.parse(json)

        assertEquals("simple_field", field.name)
        assertEquals("Simple Field", field.displayName)
        assertNull(field.uiType)
        assertNull(field.type)
        assertFalse(field.hidden)
        assertNull(field.validations)
        assertNull(field.candidates)
    }

    @Test
    fun `test parse with hidden false`() {
        val json = JSONObject(
            """
            {
                "name": "visible_field",
                "display_name": "Visible Field",
                "hidden": false
            }
            """.trimIndent()
        )

        val field = parser.parse(json)

        assertEquals("visible_field", field.name)
        assertFalse(field.hidden)
    }

    @Test
    fun `test parse with empty candidates array`() {
        val json = JSONObject(
            """
            {
                "name": "field",
                "display_name": "Field",
                "candidates": []
            }
            """.trimIndent()
        )

        val field = parser.parse(json)

        assertNotNull(field.candidates)
        assertEquals(0, field.candidates?.size)
    }

    @Test
    fun `test parse with multiple ui types`() {
        val testCases = listOf(
            "text" to DynamicSchemaFieldUIType.TEXT,
            "email" to DynamicSchemaFieldUIType.EMAIL,
            "list" to DynamicSchemaFieldUIType.LIST,
            "logo_list" to DynamicSchemaFieldUIType.LOGO_LIST,
            "checkbox" to DynamicSchemaFieldUIType.CHECKBOX
        )

        testCases.forEach { (value, expected) ->
            val json = JSONObject(
                """
                {
                    "name": "field",
                    "display_name": "Field",
                    "ui_type": "$value"
                }
                """.trimIndent()
            )

            val field = parser.parse(json)
            assertEquals(expected, field.uiType)
        }
    }

    @Test
    fun `test parse with multiple field types`() {
        val testCases = listOf(
            "string" to DynamicSchemaFieldType.STRING,
            "enum" to DynamicSchemaFieldType.ENUM,
            "banks" to DynamicSchemaFieldType.BANKS,
            "boolean" to DynamicSchemaFieldType.BOOLEAN
        )

        testCases.forEach { (value, expected) ->
            val json = JSONObject(
                """
                {
                    "name": "field",
                    "display_name": "Field",
                    "type": "$value"
                }
                """.trimIndent()
            )

            val field = parser.parse(json)
            assertEquals(expected, field.type)
        }
    }
}
