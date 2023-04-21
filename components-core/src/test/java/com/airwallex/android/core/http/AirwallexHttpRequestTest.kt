package com.airwallex.android.core.http

import com.airwallex.android.core.AirwallexPlugins
import org.junit.Test
import kotlin.test.assertEquals

class AirwallexHttpRequestTest {

    @Test
    fun contentTypeTest() {
        val contentType = AirwallexHttpRequest.createGet(
            AirwallexPlugins.environment.baseUrl(),
            null
        ).contentType
        assertEquals("application/json; charset=UTF-8", contentType)
    }

    @Test
    fun headersTest() {
        val headers = AirwallexHttpRequest.createGet(
            AirwallexPlugins.environment.baseUrl(),
            null
        ).headers
        assertEquals("Airwallex-Android-SDK", headers["User-Agent"])
    }

    @Test
    fun urlTest() {
        val baseUrl = AirwallexPlugins.environment.baseUrl()
        val url = AirwallexHttpRequest.createGet(
            url = baseUrl,
            options = null
        ).url

        assertEquals(baseUrl, url)
    }

    @Test
    fun equalTest() {
        val params = mapOf("aaa" to "123")

        assertEquals(
            AirwallexHttpRequest.createPost(
                AirwallexPlugins.environment.baseUrl(),
                null,
                params
            ).toString(),
            AirwallexHttpRequest.createPost(
                AirwallexPlugins.environment.baseUrl(),
                null,
                params
            ).toString()
        )
    }
}
