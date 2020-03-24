package com.airwallex.android

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class AirwallexHttpRequestTest {

    @Test
    fun httpRequestGetMethodTest() {
        val method: AirwallexHttpRequest.Method = AirwallexHttpRequest.Method.GET
        val url = "https://api-staging.airwallex.com"
        val request: AirwallexHttpRequest =
            AirwallexHttpRequest.Builder("https://api-staging.airwallex.com", method)
                .addHeader("name", "value")
                .build()

        assertEquals(url, request.url)
        assertEquals(method.toString(), request.method.toString())
        assertEquals(1, request.allHeaders.size)
    }

    @Test
    fun httpRequestPostMethodTest() {
        val method: AirwallexHttpRequest.Method = AirwallexHttpRequest.Method.POST
        val url = "https://api-staging.airwallex.com"
        val contentType = "application/json"
        val content = "abc"
        val request: AirwallexHttpRequest =
            AirwallexHttpRequest.Builder("https://api-staging.airwallex.com", method)
                .addHeader("name", "value")
                .setBody(
                    AirwallexHttpBody(
                        contentType,
                        content
                    )
                )
                .build()

        assertEquals(url, request.url)
        assertEquals(method.toString(), request.method.toString())
        assertEquals(1, request.allHeaders.size)
        assertEquals(contentType, request.body!!.contentType)
        assertEquals(content, request.body.content)
    }
}
