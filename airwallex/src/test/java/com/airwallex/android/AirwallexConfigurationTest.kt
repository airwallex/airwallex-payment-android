package com.airwallex.android

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class AirwallexConfigurationTest {

    @Test
    fun configurationTest() {
        val enableLogging = true
        val baseUrl = "https://staging-pci-api.airwallex.com"

        val configuration = AirwallexConfiguration.Builder()
            .enableLogging(enableLogging)
            .setBaseUrl(baseUrl)
            .build()

        assertEquals(enableLogging, configuration.enableLogging)
        assertEquals(baseUrl, configuration.baseUrl)
    }
}
