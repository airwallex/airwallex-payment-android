package com.airwallex.android

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class AirwallexHttpResponseTest {

    @Test
    fun httpResponseTest() {
        val headers: MutableMap<String, String?> = mutableMapOf("name" to "value")
        val statusCode = 200
        val message = "abc"
        val isSuccessful = true

        val response: AirwallexHttpResponse = AirwallexHttpResponse.Builder()
            .setHeaders(headers)
            .setStatusCode(statusCode)
            .setIsSuccessful(true)
            .setMessage(message)
            .build()

        assertEquals(statusCode, response.statusCode)
        assertEquals(message, response.message)
        assertEquals(1, response.allHeaders.size)
        assertEquals(statusCode, response.statusCode)
        assertEquals(isSuccessful, response.isSuccessful)
    }

}
