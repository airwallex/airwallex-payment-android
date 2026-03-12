package com.airwallex.android.core

import android.graphics.Color
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

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

    @Test
    fun `test configuration with default payment appearance`() {
        val configuration = AirwallexConfiguration.Builder()
            .build()

        assertNull(configuration.paymentAppearance)
    }

    @Test
    fun `test configuration with payment appearance theme color only`() {
        val themeColor = Color.parseColor("#612FFF")
        val appearance = PaymentAppearance(themeColor = themeColor)

        val configuration = AirwallexConfiguration.Builder()
            .setPaymentAppearance(appearance)
            .build()

        assertNotNull(configuration.paymentAppearance)
        assertEquals(themeColor, configuration.paymentAppearance?.themeColor)
        assertNull(configuration.paymentAppearance?.isDarkTheme)
    }

    @Test
    fun `test configuration with payment appearance dark theme only`() {
        val isDark = true
        val appearance = PaymentAppearance(isDarkTheme = isDark)

        val configuration = AirwallexConfiguration.Builder()
            .setPaymentAppearance(appearance)
            .build()

        assertNotNull(configuration.paymentAppearance)
        assertNull(configuration.paymentAppearance?.themeColor)
        assertEquals(isDark, configuration.paymentAppearance?.isDarkTheme)
    }

    @Test
    fun `test configuration with full payment appearance`() {
        val themeColor = Color.parseColor("#DA8C21")
        val isDark = true
        val appearance = PaymentAppearance(
            themeColor = themeColor,
            isDarkTheme = isDark
        )

        val configuration = AirwallexConfiguration.Builder()
            .setPaymentAppearance(appearance)
            .build()

        assertNotNull(configuration.paymentAppearance)
        assertEquals(themeColor, configuration.paymentAppearance?.themeColor)
        assertEquals(isDark, configuration.paymentAppearance?.isDarkTheme)
    }
}
