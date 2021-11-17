package com.airwallex.android.core.util

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

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
    fun jsonArrayToListTest() {
        assertNull(AirwallexJsonUtils.jsonArrayToList(null))

        val expectedList = listOf("a", "b", "c", "d", true)
        val convertedJsonArray = AirwallexJsonUtils.jsonArrayToList(TEST_JSON_ARRAY)
        assertEquals(expectedList, convertedJsonArray)
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
