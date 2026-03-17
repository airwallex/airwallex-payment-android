package com.airwallex.android.core.util

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AirwallexJsonUtilsTest {

    @Test
    fun optStringTest() {
        val jsonObject = JSONObject().put("key", "value")
        assertEquals("value", AirwallexJsonUtils.optString(jsonObject, "key"))

        jsonObject.put("key", "null")
        assertNull(AirwallexJsonUtils.optString(jsonObject, "key"))

        jsonObject.put("key", "value")
        val ob = AirwallexJsonUtils.optString(jsonObject, "nokeyshere")
        assertNull(ob)
    }

    @Test
    fun `optString with null jsonObject`() {
        assertNull(AirwallexJsonUtils.optString(null, "key"))
    }

    @Test
    fun `optString with empty string`() {
        val jsonObject = JSONObject().put("key", "")
        assertNull(AirwallexJsonUtils.optString(jsonObject, "key"))
    }

    @Test
    fun optIntTest() {
        val jsonObject = JSONObject().put("count", 42)
        assertEquals(42, AirwallexJsonUtils.optInt(jsonObject, "count"))

        assertNull(AirwallexJsonUtils.optInt(jsonObject, "missing"))
    }

    @Test
    fun optBooleanTest() {
        val jsonObject = JSONObject()
            .put("trueField", true)
            .put("falseField", false)

        assertTrue(AirwallexJsonUtils.optBoolean(jsonObject, "trueField"))
        assertFalse(AirwallexJsonUtils.optBoolean(jsonObject, "falseField"))
        assertFalse(AirwallexJsonUtils.optBoolean(jsonObject, "missing"))
    }

    @Test
    fun optDoubleTest() {
        val jsonObject = JSONObject().put("price", 19.99)
        assertEquals(19.99, AirwallexJsonUtils.optDouble(jsonObject, "price"))

        assertNull(AirwallexJsonUtils.optDouble(jsonObject, "missing"))
    }

    @Test
    fun optMapTest() {
        val jsonObject = JSONObject().put(
            "data",
            JSONObject().put("nested", "value")
        )

        val result = AirwallexJsonUtils.optMap(jsonObject, "data")
        assertNotNull(result)
        assertEquals("value", result["nested"])

        assertNull(AirwallexJsonUtils.optMap(jsonObject, "missing"))
    }

    @Test
    fun jsonObjectToMapTest() {
        assertNull(AirwallexJsonUtils.jsonObjectToMap(null))

        val expectedMap = mapOf(
            "a" to "a",
            "b" to "b",
            "c" to true,
            "d" to 123
        )

        val mappedObject = AirwallexJsonUtils.jsonObjectToMap(TEST_JSON_OBJECT)
        assertEquals(expectedMap, mappedObject)
    }

    @Test
    fun `jsonObjectToMap with nested objects`() {
        val json = JSONObject(
            """
            {
                "outer": {
                    "inner": "value",
                    "number": 123
                }
            }
            """.trimIndent()
        )

        val result = AirwallexJsonUtils.jsonObjectToMap(json)
        assertNotNull(result)
        val outer = result["outer"] as? Map<*, *>
        assertNotNull(outer)
        assertEquals("value", outer["inner"])
        assertEquals(123, outer["number"])
    }

    @Test
    fun `jsonObjectToMap with nested arrays`() {
        val json = JSONObject(
            """
            {
                "items": ["a", "b", "c"]
            }
            """.trimIndent()
        )

        val result = AirwallexJsonUtils.jsonObjectToMap(json)
        assertNotNull(result)
        val items = result["items"] as? List<*>
        assertNotNull(items)
        assertEquals(listOf("a", "b", "c"), items)
    }

    // TODO: not sure the actual expected
    @Ignore
    @Test
    fun `jsonObjectToMap with null values filtered`() {
        val json = JSONObject(
            """
            {
                "key1": "value1",
                "key2": null,
                "key3": "value3"
            }
            """.trimIndent()
        )
        val result = AirwallexJsonUtils.jsonObjectToMap(json)
        assertNotNull(result)
        assertEquals("value1", result["key1"])
        assertFalse(result.containsKey("key2"))
        assertEquals("value3", result["key3"])
    }

    @Test
    fun jsonArrayToListTest() {
        assertNull(AirwallexJsonUtils.jsonArrayToList(null))

        val expectedList = listOf("a", "b", "c", "d", true)
        val convertedJsonArray = AirwallexJsonUtils.jsonArrayToList(TEST_JSON_ARRAY)
        assertEquals(expectedList, convertedJsonArray)
    }

    @Test
    fun `jsonArrayToList with nested arrays`() {
        val json = JSONArray(
            """
            [
                ["nested", "array"],
                "single"
            ]
            """.trimIndent()
        )

        val result = AirwallexJsonUtils.jsonArrayToList(json)
        assertNotNull(result)
        assertEquals(2, result.size)
        val nested = result[0] as? List<*>
        assertNotNull(nested)
        assertEquals(listOf("nested", "array"), nested)
        assertEquals("single", result[1])
    }

    @Test
    fun `jsonArrayToList with nested objects`() {
        val json = JSONArray(
            """
            [
                {"key": "value"},
                "plain"
            ]
            """.trimIndent()
        )

        val result = AirwallexJsonUtils.jsonArrayToList(json)
        assertNotNull(result)
        assertEquals(2, result.size)
        val obj = result[0] as? Map<*, *>
        assertNotNull(obj)
        assertEquals("value", obj["key"])
        assertEquals("plain", result[1])
    }

    @Test
    fun mapToJsonObjectTest() {
        assertNull(AirwallexJsonUtils.mapToJsonObject(null))

        val map = mapOf(
            "string" to "value",
            "number" to 42,
            "boolean" to true,
            "double" to 3.14
        )

        val result = AirwallexJsonUtils.mapToJsonObject(map)
        assertNotNull(result)
        assertEquals("value", result.getString("string"))
        assertEquals(42, result.getInt("number"))
        assertEquals(true, result.getBoolean("boolean"))
        assertEquals(3.14, result.getDouble("double"))
    }

    @Test
    fun `mapToJsonObject with nested map`() {
        val map = mapOf(
            "outer" to mapOf(
                "inner" to "value"
            )
        )

        val result = AirwallexJsonUtils.mapToJsonObject(map)
        assertNotNull(result)
        val outer = result.getJSONObject("outer")
        assertEquals("value", outer.getString("inner"))
    }

    @Test
    fun `mapToJsonObject with list`() {
        val map = mapOf(
            "items" to listOf("a", "b", "c")
        )

        val result = AirwallexJsonUtils.mapToJsonObject(map)
        assertNotNull(result)
        val items = result.getJSONArray("items")
        assertEquals(3, items.length())
        assertEquals("a", items.getString(0))
    }

    @Test
    fun `mapToJsonObject with null values skipped`() {
        val map = mapOf(
            "key1" to "value1",
            "key2" to null,
            "key3" to "value3"
        )

        val result = AirwallexJsonUtils.mapToJsonObject(map)
        assertNotNull(result)
        assertTrue(result.has("key1"))
        assertFalse(result.has("key2"))
        assertTrue(result.has("key3"))
    }

    @Test
    fun `mapToJsonObject with nested list containing maps`() {
        val map = mapOf(
            "items" to listOf(
                mapOf("id" to 1),
                mapOf("id" to 2)
            )
        )

        val result = AirwallexJsonUtils.mapToJsonObject(map)
        assertNotNull(result)
        val items = result.getJSONArray("items")
        assertEquals(2, items.length())
        assertEquals(1, items.getJSONObject(0).getInt("id"))
    }

    private companion object {

        private val TEST_JSON_ARRAY = JSONArray(
            """
            ["a", "b", "c", "d", true]
            """.trimIndent()
        )

        private val TEST_JSON_OBJECT = JSONObject(
            """
            {
                "a": "a",
                "b": "b",
                "c": true,
                "d": 123
            }
            """.trimIndent()
        )
    }
}
