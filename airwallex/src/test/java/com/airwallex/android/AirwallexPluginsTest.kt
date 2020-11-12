package com.airwallex.android

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class AirwallexPluginsTest {

    @Test
    fun restClientTest() {
        AirwallexPlugins.initialize(
            AirwallexConfiguration.Builder()
                .enableLogging(true)
                .setEnvironment(Environment.DEMO)
                .build()
        )
        assertEquals(true, AirwallexPlugins.enableLogging)
        assertEquals(Environment.DEMO, AirwallexPlugins.environment)
    }
}
