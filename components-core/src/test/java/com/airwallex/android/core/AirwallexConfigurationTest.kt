package com.airwallex.android.core

import io.mockk.mockk
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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

    @Test
    fun `defaults when no setters are called`() {
        val configuration = AirwallexConfiguration.Builder().build()

        assertFalse(configuration.enableLogging)
        assertEquals(Environment.PRODUCTION, configuration.environment)
        assertEquals(emptyList(), configuration.supportComponentProviders)
        assertTrue(configuration.enableAnalytics)
        assertFalse(configuration.saveLogToLocal)
        assertEquals(RedirectMode.CUSTOM_TAB, configuration.redirectMode)
    }

    @Test
    fun `enableLogging false is honored`() {
        val configuration = AirwallexConfiguration.Builder()
            .enableLogging(true)
            .enableLogging(false)
            .build()

        assertFalse(configuration.enableLogging)
    }

    @Test
    fun `disableAnalytics flips the default`() {
        val configuration = AirwallexConfiguration.Builder()
            .disableAnalytics()
            .build()

        assertFalse(configuration.enableAnalytics)
    }

    @Test
    fun `saveLogToLocal persists into the built configuration`() {
        val configuration = AirwallexConfiguration.Builder()
            .saveLogToLocal(true)
            .build()

        assertTrue(configuration.saveLogToLocal)
    }

    @Test
    fun `setRedirectMode persists into the built configuration`() {
        val custom = AirwallexConfiguration.Builder()
            .setRedirectMode(RedirectMode.EXTERNAL_BROWSER)
            .build()
        assertEquals(RedirectMode.EXTERNAL_BROWSER, custom.redirectMode)

        val sheet = AirwallexConfiguration.Builder()
            .setRedirectMode(RedirectMode.CUSTOM_TAB_BOTTOM_SHEET)
            .build()
        assertEquals(RedirectMode.CUSTOM_TAB_BOTTOM_SHEET, sheet.redirectMode)
    }

    @Test
    fun `setSupportComponentProviders passes the exact list through`() {
        val provider = mockk<ActionComponentProvider<ActionComponent>>()
        val providers = listOf(provider)

        val configuration = AirwallexConfiguration.Builder()
            .setSupportComponentProviders(providers)
            .build()

        assertEquals(providers, configuration.supportComponentProviders)
    }

    @Test
    fun `every Environment value is reachable via the Builder`() {
        Environment.values().forEach { env ->
            val configuration = AirwallexConfiguration.Builder()
                .setEnvironment(env)
                .build()
            assertEquals(env, configuration.environment)
        }
    }

    @Test
    fun `all setters compose into one configuration`() {
        val provider = mockk<ActionComponentProvider<ActionComponent>>()

        val configuration = AirwallexConfiguration.Builder()
            .enableLogging(true)
            .setEnvironment(Environment.STAGING)
            .setSupportComponentProviders(listOf(provider))
            .disableAnalytics()
            .saveLogToLocal(true)
            .setRedirectMode(RedirectMode.EXTERNAL_BROWSER)
            .build()

        assertTrue(configuration.enableLogging)
        assertEquals(Environment.STAGING, configuration.environment)
        assertEquals(listOf(provider), configuration.supportComponentProviders)
        assertFalse(configuration.enableAnalytics)
        assertTrue(configuration.saveLogToLocal)
        assertEquals(RedirectMode.EXTERNAL_BROWSER, configuration.redirectMode)
    }

    @Test
    fun `data class equality holds for identical configurations`() {
        val a = AirwallexConfiguration.Builder()
            .enableLogging(true)
            .setEnvironment(Environment.DEMO)
            .build()
        val b = AirwallexConfiguration.Builder()
            .enableLogging(true)
            .setEnvironment(Environment.DEMO)
            .build()

        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }
}
