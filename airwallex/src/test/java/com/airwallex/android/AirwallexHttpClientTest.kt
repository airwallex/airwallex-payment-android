package com.airwallex.android

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class AirwallexHttpClientTest {

    @Test
    fun httpExecuteWithResponse() {
        val response = AirwallexPlugins.httpClient.execute(
            AirwallexHttpRequest.Builder(
                AirwallexApiRepository.retrievePaymentIntentUrl(
                    "https://api-staging.airwallex.com",
                    "abc"
                ),
                AirwallexHttpRequest.Method.GET
            )
                .build()
        )

        assertEquals(401, response.statusCode)
        assertEquals(false, response.isSuccessful)
        assertEquals("Unauthorized", response.message)
    }
}
