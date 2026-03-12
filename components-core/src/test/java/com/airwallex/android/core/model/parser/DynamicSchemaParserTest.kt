package com.airwallex.android.core.model.parser

import com.airwallex.android.core.model.TransactionMode
import org.json.JSONObject
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DynamicSchemaParserTest {

    private val parser = DynamicSchemaParser()

    @Test
    fun `test parse with all fields`() {
        val json = JSONObject(
            """
            {
                "transaction_mode": "oneoff",
                "fields": [
                    {
                        "name": "field1",
                        "display_name": "Field One",
                        "ui_type": "text",
                        "type": "string",
                        "hidden": false
                    },
                    {
                        "name": "field2",
                        "display_name": "Field Two",
                        "ui_type": "select",
                        "type": "enum",
                        "hidden": true
                    }
                ]
            }
            """.trimIndent()
        )

        val schema = parser.parse(json)

        assertEquals(TransactionMode.ONE_OFF, schema.transactionMode)
        assertNotNull(schema.fields)
        assertEquals(2, schema.fields?.size)
        assertEquals("field1", schema.fields?.get(0)?.name)
        assertEquals("field2", schema.fields?.get(1)?.name)
    }

    @Test
    fun `test parse with missing fields array`() {
        val json = JSONObject(
            """
            {
                "transaction_mode": "recurring"
            }
            """.trimIndent()
        )

        val schema = parser.parse(json)

        assertEquals(TransactionMode.RECURRING, schema.transactionMode)
        assertNotNull(schema.fields)
        assertEquals(0, schema.fields?.size)
    }

    @Test
    fun `test parse with empty json`() {
        val json = JSONObject("{}")

        val schema = parser.parse(json)

        assertNull(schema.transactionMode)
        assertNotNull(schema.fields)
        assertEquals(0, schema.fields?.size)
    }
}
