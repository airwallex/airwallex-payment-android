package com.airwallex.android

import android.content.Context
import androidx.test.core.app.ApplicationProvider
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

        val context = ApplicationProvider.getApplicationContext<Context>()
        val configuration = AirwallexConfiguration.Builder(context)
            .enableLogging(enableLogging)
            .setBaseUrl(baseUrl)
            .build()

        assertEquals(enableLogging, configuration.enableLogging)
        assertEquals(baseUrl, configuration.baseUrl)
    }
}
