package com.airwallex.android.core.http

import com.airwallex.android.core.AirwallexPlugins
import com.airwallex.android.core.model.Options
import org.junit.Test
import kotlin.test.assertEquals

class AirwallexHttpRequestTest {

    private val options = Options(clientSecret = "")

    @Test
    fun contentTypeTest() {
        val contentType = AirwallexHttpRequest.createGet(
            AirwallexPlugins.environment.baseUrl(),
            options
        ).contentType
        assertEquals("application/json; charset=UTF-8", contentType)
    }

    @Test
    fun urlTest() {
        val url = AirwallexHttpRequest.createGet(
            url = AirwallexPlugins.environment.baseUrl(),
            options = options
        ).url

        assertEquals("https://pci-api.airwallex.com", url)
    }

    @Test
    fun equalTest() {
        val params = mapOf("aaa" to "123")

        assertEquals(
            AirwallexHttpRequest.createPost(
                AirwallexPlugins.environment.baseUrl(),
                options,
                params
            ).toString(),
            AirwallexHttpRequest.createPost(
                AirwallexPlugins.environment.baseUrl(),
                options,
                params
            ).toString()
        )
    }
}
