package com.airwallex.android.core

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class EnvironmentTest {

    @Test
    fun baseUrlTest() {
        assertNotNull(Environment.STAGING.baseUrl())
        assertEquals("https://api-staging.airwallex.com", Environment.STAGING.baseUrl())

        assertNotNull(Environment.DEMO.baseUrl())
        assertEquals("https://api-demo.airwallex.com", Environment.DEMO.baseUrl())

        assertNotNull(Environment.PRODUCTION.baseUrl())
        assertEquals("https://api.airwallex.com", Environment.PRODUCTION.baseUrl())
    }

    @Test
    fun trackerUrl() {
        assertNotNull(Environment.STAGING.trackerUrl())
        assertEquals("https://api-staging.airwallex.com/api/v1/checkout", Environment.STAGING.trackerUrl())

        assertNotNull(Environment.DEMO.trackerUrl())
        assertEquals("https://api-demo.airwallex.com/api/v1/checkout", Environment.DEMO.trackerUrl())

        assertNotNull(Environment.PRODUCTION.trackerUrl())
        assertEquals("https://api.airwallex.com/api/v1/checkout", Environment.PRODUCTION.trackerUrl())
    }
}
