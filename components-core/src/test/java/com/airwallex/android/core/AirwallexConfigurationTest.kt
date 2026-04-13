package com.airwallex.android.core

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class AirwallexConfigurationTest {

    @Test
    fun configurationTest() {
        val enableLogging = true
        val demoBaseUrl = "https://api-demo.airwallex.com"

        val configuration = AirwallexConfiguration.Builder()
            .enableLogging(enableLogging)
            .setEnvironment(Environment.DEMO)
            .build()

        assertEquals(enableLogging, configuration.enableLogging)
        assertEquals(demoBaseUrl, configuration.environment.baseUrl())
    }
}
