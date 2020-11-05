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
        val demoBaseUrl = "https://demo-pci-api.airwallex.com"

        val configuration = AirwallexConfiguration.Builder()
            .enableLogging(enableLogging)
            .setEnvironment(Environment.DEMO)
            .build()

        assertEquals(enableLogging, configuration.enableLogging)
        assertEquals(demoBaseUrl, configuration.environment.baseUrl())
    }
}
