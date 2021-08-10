package com.airwallex.android.core

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class EnvironmentTest {

    @Test
    fun baseUrlTest() {
        assertNotNull(Environment.STAGING.baseUrl())
        assertEquals("https://pci-api-staging.airwallex.com", Environment.STAGING.baseUrl())

        assertNotNull(Environment.DEMO.baseUrl())
        assertEquals("https://pci-api-demo.airwallex.com", Environment.DEMO.baseUrl())

        assertNotNull(Environment.PRODUCTION.baseUrl())
        assertEquals("https://pci-api.airwallex.com", Environment.PRODUCTION.baseUrl())
    }

    @Test
    fun cybsUrl() {
        assertNotNull(Environment.STAGING.cybsUrl())
        assertEquals("https://pci-api-staging.airwallex.com/pa/webhook/cybs", Environment.STAGING.cybsUrl())

        assertNotNull(Environment.DEMO.cybsUrl())
        assertEquals("https://pci-api-demo.airwallex.com/pa/webhook/cybs", Environment.DEMO.cybsUrl())

        assertNotNull(Environment.PRODUCTION.cybsUrl())
        assertEquals("https://pci-api.airwallex.com/pa/webhook/cybs", Environment.PRODUCTION.cybsUrl())
    }

    @Test
    fun trackerUrl() {
        assertNotNull(Environment.STAGING.trackerUrl())
        assertEquals("https://pci-api-staging.airwallex.com/api/v1/checkout", Environment.STAGING.trackerUrl())

        assertNotNull(Environment.DEMO.trackerUrl())
        assertEquals("https://pci-api-demo.airwallex.com/api/v1/checkout", Environment.DEMO.trackerUrl())

        assertNotNull(Environment.PRODUCTION.trackerUrl())
        assertEquals("https://pci-api.airwallex.com/api/v1/checkout", Environment.PRODUCTION.trackerUrl())
    }
}
