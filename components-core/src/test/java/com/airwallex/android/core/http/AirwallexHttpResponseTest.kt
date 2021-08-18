package com.airwallex.android.core.http

import org.junit.Test
import kotlin.test.assertEquals

class AirwallexHttpResponseTest {

    @Test
    fun responseTest() {
        val response = AirwallexHttpResponse(
            code = 200,
            body = "{\"aaa\": \"bbb\"}",
            headers = mapOf("aaa" to listOf("123"))
        )

        assertEquals(200, response.code)
        assertEquals("{\"aaa\": \"bbb\"}", response.body)
        assertEquals("{\"aaa\":\"bbb\"}", response.responseJson.toString())
        assertEquals(
            "Status Code: 200, Trace-Id: null \n" +
                " {\"aaa\": \"bbb\"}",
            response.toString()
        )
    }
}
