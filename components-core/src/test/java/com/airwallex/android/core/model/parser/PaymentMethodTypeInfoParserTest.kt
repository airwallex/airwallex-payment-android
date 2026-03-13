package com.airwallex.android.core.model.parser

import org.json.JSONObject
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PaymentMethodTypeInfoParserTest {

    private val parser = PaymentMethodTypeInfoParser()

    @Test
    fun `test parse with all fields`() {
        val json = JSONObject(
            """
            {
                "name": "wechatpay",
                "display_name": "WeChat Pay",
                "logos": {
                    "png": "https://example.com/wechat.png",
                    "svg": "https://example.com/wechat.svg"
                },
                "has_schema": true,
                "field_schemas": [
                    {
                        "transaction_mode": "oneoff",
                        "fields": [
                            {
                                "name": "phone",
                                "display_name": "Phone Number",
                                "ui_type": "phone",
                                "type": "string",
                                "hidden": false
                            }
                        ]
                    },
                    {
                        "transaction_mode": "recurring",
                        "fields": []
                    }
                ]
            }
            """.trimIndent()
        )

        val typeInfo = parser.parse(json)

        assertEquals("wechatpay", typeInfo.name)
        assertEquals("WeChat Pay", typeInfo.displayName)
        assertNotNull(typeInfo.logos)
        assertEquals("https://example.com/wechat.png", typeInfo.logos?.png)
        assertEquals("https://example.com/wechat.svg", typeInfo.logos?.svg)
        assertEquals(true, typeInfo.hasSchema)
        assertNotNull(typeInfo.fieldSchemas)
        assertEquals(2, typeInfo.fieldSchemas?.size)
    }

    @Test
    fun `test parse without logos`() {
        val json = JSONObject(
            """
            {
                "name": "alipay",
                "display_name": "Alipay",
                "has_schema": false
            }
            """.trimIndent()
        )

        val typeInfo = parser.parse(json)

        assertEquals("alipay", typeInfo.name)
        assertEquals("Alipay", typeInfo.displayName)
        assertNull(typeInfo.logos)
        assertEquals(false, typeInfo.hasSchema)
        assertNotNull(typeInfo.fieldSchemas)
        assertEquals(0, typeInfo.fieldSchemas?.size)
    }

    @Test
    fun `test parse without field_schemas`() {
        val json = JSONObject(
            """
            {
                "name": "paypal",
                "display_name": "PayPal",
                "has_schema": true
            }
            """.trimIndent()
        )

        val typeInfo = parser.parse(json)

        assertEquals("paypal", typeInfo.name)
        assertEquals("PayPal", typeInfo.displayName)
        assertTrue(typeInfo.hasSchema == true)
        assertNotNull(typeInfo.fieldSchemas)
        assertEquals(0, typeInfo.fieldSchemas?.size)
    }

    @Test
    fun `test parse with empty field_schemas array`() {
        val json = JSONObject(
            """
            {
                "name": "stripe",
                "display_name": "Stripe",
                "has_schema": false,
                "field_schemas": []
            }
            """.trimIndent()
        )

        val typeInfo = parser.parse(json)

        assertEquals("stripe", typeInfo.name)
        assertFalse(typeInfo.hasSchema == true)
        assertNotNull(typeInfo.fieldSchemas)
        assertEquals(0, typeInfo.fieldSchemas?.size)
    }

    @Test
    fun `test parse with empty json`() {
        val json = JSONObject("{}")

        val typeInfo = parser.parse(json)

        assertNull(typeInfo.name)
        assertNull(typeInfo.displayName)
        assertNull(typeInfo.logos)
        assertEquals(false, typeInfo.hasSchema)
        assertNotNull(typeInfo.fieldSchemas)
        assertEquals(0, typeInfo.fieldSchemas?.size)
    }

    @Test
    fun `test parse with logos but no field_schemas`() {
        val json = JSONObject(
            """
            {
                "name": "card",
                "display_name": "Card",
                "logos": {
                    "png": "https://example.com/card.png"
                },
                "has_schema": true
            }
            """.trimIndent()
        )

        val typeInfo = parser.parse(json)

        assertEquals("card", typeInfo.name)
        assertEquals("Card", typeInfo.displayName)
        assertNotNull(typeInfo.logos)
        assertEquals("https://example.com/card.png", typeInfo.logos?.png)
        assertTrue(typeInfo.hasSchema == true)
    }
}
