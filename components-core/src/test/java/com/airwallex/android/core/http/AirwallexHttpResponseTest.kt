package com.airwallex.android.core.http

import org.junit.Test
import kotlin.test.assertEquals

class AirwallexHttpResponseTest {

    @Test
    fun responseCodeTest() {
        val response = AirwallexHttpResponse(
            code = 200,
            body = "{}",
            headers = mapOf("aaa" to listOf("123"))
        )

        assertEquals(200, response.code)
    }

    @Test
    fun responseBodyTest() {
        val response = AirwallexHttpResponse(
            code = 200,
            body = "{}",
            headers = mapOf("aaa" to listOf("123"))
        )

        assertEquals("{}", response.body)
    }
}
