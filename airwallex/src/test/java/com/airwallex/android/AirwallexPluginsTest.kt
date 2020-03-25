package com.airwallex.android

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@RunWith(RobolectricTestRunner::class)
class AirwallexPluginsTest {

    @Test
    fun restClientTest() {
        val restClient = AirwallexPlugins.restClient

        assertNotNull(restClient.builder)
        assertEquals(1, restClient.builder.interceptors().size)
    }
}
