package com.airwallex.android.core.model.parser

import org.json.JSONObject
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BankResponseParserTest {

    private val parser = BankResponseParser()

    @Test
    fun `test parse bank response with multiple banks`() {
        val json = JSONObject(
            """
            {
                "has_more": true,
                "items": [
                    {
                        "bank_name": "bank_1",
                        "display_name": "Bank One",
                        "resources": {
                            "logos": {
                                "png": "https://example.com/bank1.png",
                                "svg": "https://example.com/bank1.svg"
                            }
                        }
                    },
                    {
                        "bank_name": "bank_2",
                        "display_name": "Bank Two",
                        "resources": {
                            "logos": {
                                "png": "https://example.com/bank2.png",
                                "svg": "https://example.com/bank2.svg"
                            }
                        }
                    },
                    {
                        "bank_name": "bank_3",
                        "display_name": "Bank Three"
                    }
                ]
            }
            """.trimIndent()
        )

        val response = parser.parse(json)

        assertTrue(response.hasMore)
        assertNotNull(response.items)
        assertEquals(3, response.items?.size)

        // Verify first bank
        val bank1 = response.items?.get(0)
        assertEquals("bank_1", bank1?.name)
        assertEquals("Bank One", bank1?.displayName)
        assertNotNull(bank1?.resources)
        assertEquals("https://example.com/bank1.png", bank1?.resources?.logos?.png)

        // Verify second bank
        val bank2 = response.items?.get(1)
        assertEquals("bank_2", bank2?.name)
        assertEquals("Bank Two", bank2?.displayName)
        assertNotNull(bank2?.resources)
        assertEquals("https://example.com/bank2.png", bank2?.resources?.logos?.png)

        // Verify third bank
        val bank3 = response.items?.get(2)
        assertEquals("bank_3", bank3?.name)
        assertEquals("Bank Three", bank3?.displayName)
    }

    @Test
    fun `test parse bank response with empty items`() {
        val json = JSONObject(
            """
            {
                "has_more": false,
                "items": []
            }
            """.trimIndent()
        )

        val response = parser.parse(json)

        assertFalse(response.hasMore)
        assertNotNull(response.items)
        assertEquals(0, response.items?.size)
    }

//    @Test
//    fun `test parse bank response without items field`() {
//        val json = JSONObject(
//            """
//            {
//                "has_more": false
//            }
//            """.trimIndent()
//        )
//
//        val response = parser.parse(json)
//
//        assertFalse(response.hasMore)
//        assertNotNull(response.items)
//        assertEquals(0, response.items?.size)
//    }
//
//    @Test
//    fun `test parse bank response with has_more true`() {
//        val json = JSONObject(
//            """
//            {
//                "has_more": true,
//                "items": [
//                    {
//                        "bank_name": "test_bank",
//                        "display_name": "Test Bank"
//                    }
//                ]
//            }
//            """.trimIndent()
//        )
//
//        val response = parser.parse(json)
//
//        assertTrue(response.hasMore)
//        assertEquals(1, response.items?.size)
//    }
//
//    @Test
//    fun `test parse bank response with has_more false`() {
//        val json = JSONObject(
//            """
//            {
//                "has_more": false,
//                "items": [
//                    {
//                        "bank_name": "test_bank",
//                        "display_name": "Test Bank"
//                    }
//                ]
//            }
//            """.trimIndent()
//        )
//
//        val response = parser.parse(json)
//
//        assertFalse(response.hasMore)
//        assertEquals(1, response.items?.size)
//    }
//
//    @Test
//    fun `test parse bank response with missing has_more defaults to false`() {
//        val json = JSONObject(
//            """
//            {
//                "items": [
//                    {
//                        "bank_name": "test_bank",
//                        "display_name": "Test Bank"
//                    }
//                ]
//            }
//            """.trimIndent()
//        )
//
//        val response = parser.parse(json)
//
//        assertFalse(response.hasMore)
//        assertEquals(1, response.items?.size)
//    }
//
//    @Test
//    fun `test parse bank response with single bank`() {
//        val json = JSONObject(
//            """
//            {
//                "has_more": false,
//                "items": [
//                    {
//                        "bank_name": "single_bank",
//                        "display_name": "Single Bank",
//                        "resources": {
//                            "logos": {
//                                "png": "https://example.com/single.png",
//                                "svg": "https://example.com/single.svg"
//                            }
//                        }
//                    }
//                ]
//            }
//            """.trimIndent()
//        )
//
//        val response = parser.parse(json)
//
//        assertFalse(response.hasMore)
//        assertNotNull(response.items)
//        assertEquals(1, response.items?.size)
//
//        val bank = response.items?.first()
//        assertEquals("single_bank", bank?.name)
//        assertEquals("Single Bank", bank?.displayName)
//        assertNotNull(bank?.resources)
//        assertEquals("https://example.com/single.png", bank?.resources?.logos?.png)
//        assertEquals("https://example.com/single.svg", bank?.resources?.logos?.svg)
//    }
//
//    @Test
//    fun `test parse bank response with null items in array filters them out`() {
//        val json = JSONObject(
//            """
//            {
//                "has_more": false,
//                "items": [
//                    {
//                        "bank_name": "bank_1",
//                        "display_name": "Bank One"
//                    },
//                    null,
//                    {
//                        "bank_name": "bank_2",
//                        "display_name": "Bank Two"
//                    }
//                ]
//            }
//            """.trimIndent()
//        )
//
//        val response = parser.parse(json)
//
//        assertFalse(response.hasMore)
//        assertNotNull(response.items)
//        // Should filter out the null item
//        assertEquals(2, response.items?.size)
//        assertEquals("bank_1", response.items?.get(0)?.name)
//        assertEquals("bank_2", response.items?.get(1)?.name)
//    }

    @Test
    fun `test parse bank response with completely empty json`() {
        val json = JSONObject("{}")

        val response = parser.parse(json)

        assertFalse(response.hasMore)
        assertNotNull(response.items)
        assertEquals(0, response.items?.size)
    }

    @Test
    fun `test parse bank response with mixed banks with and without resources`() {
        val json = JSONObject(
            """
            {
                "has_more": true,
                "items": [
                    {
                        "bank_name": "full_bank",
                        "display_name": "Full Bank",
                        "resources": {
                            "logos": {
                                "png": "https://example.com/full.png",
                                "svg": "https://example.com/full.svg"
                            }
                        }
                    },
                    {
                        "bank_name": "simple_bank",
                        "display_name": "Simple Bank"
                    },
                    {
                        "bank_name": "partial_bank",
                        "display_name": "Partial Bank",
                        "resources": {
                            "logos": {
                                "png": "https://example.com/partial.png"
                            }
                        }
                    }
                ]
            }
            """.trimIndent()
        )

        val response = parser.parse(json)

        assertTrue(response.hasMore)
        assertNotNull(response.items)
        assertEquals(3, response.items?.size)

        // Full bank with all resources
        assertEquals("full_bank", response.items?.get(0)?.name)
        assertNotNull(response.items?.get(0)?.resources)

        // Simple bank without resources
        assertEquals("simple_bank", response.items?.get(1)?.name)

        // Partial bank with partial resources
        assertEquals("partial_bank", response.items?.get(2)?.name)
        assertNotNull(response.items?.get(2)?.resources)
        assertEquals("https://example.com/partial.png", response.items?.get(2)?.resources?.logos?.png)
    }
}
