package com.airwallex.android.ui.composables

import android.graphics.Color
import androidx.compose.ui.graphics.Color as ComposeColor
import com.airwallex.android.core.AirwallexConfiguration
import com.airwallex.android.core.AirwallexPlugins
import com.airwallex.android.core.PaymentAppearance
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class AirwallexThemeConfigTest {

    @Before
    fun setUp() {
        // Initialize with default configuration to reset state
        AirwallexPlugins.initialize(AirwallexConfiguration.Builder().build())

        // Reset AirwallexThemeConfig before each test
        AirwallexThemeConfig.setDarkMode(DarkMode.SYSTEM)
    }

    @Test
    fun `test set dark mode to DARK`() {
        AirwallexThemeConfig.setDarkMode(DarkMode.DARK)
        AirwallexPlugins.initialize(AirwallexConfiguration.Builder().build())

        assertTrue(AirwallexThemeConfig.isDarkTheme)
    }

    @Test
    fun `test set dark mode to LIGHT`() {
        AirwallexThemeConfig.setDarkMode(DarkMode.LIGHT)
        AirwallexPlugins.initialize(AirwallexConfiguration.Builder().build())

        assertFalse(AirwallexThemeConfig.isDarkTheme)
    }

    @Test
    fun `test PaymentAppearance overrides programmatic dark mode`() {
        AirwallexThemeConfig.setDarkMode(DarkMode.DARK) // Set programmatically

        // Initialize with PaymentAppearance that forces light mode
        AirwallexPlugins.initialize(
            AirwallexConfiguration.Builder()
                .setPaymentAppearance(PaymentAppearance(isDarkTheme = false))
                .build()
        )

        // PaymentAppearance should win
        assertFalse(AirwallexThemeConfig.isDarkTheme)
    }

    @Test
    fun `test PaymentAppearance dark theme true`() {
        AirwallexPlugins.initialize(
            AirwallexConfiguration.Builder()
                .setPaymentAppearance(PaymentAppearance(isDarkTheme = true))
                .build()
        )

        assertTrue(AirwallexThemeConfig.isDarkTheme)
    }

    @Test
    fun `test default theme color is Ultraviolet70`() {
        AirwallexPlugins.initialize(AirwallexConfiguration.Builder().build())

        val themeColor = AirwallexThemeConfig.themeColor

        assertEquals(AirwallexColor.Ultraviolet70, themeColor)
    }

    @Test
    fun `test PaymentAppearance theme color overrides default`() {
        val customColor = Color.parseColor("#DA8C21")
        AirwallexPlugins.initialize(
            AirwallexConfiguration.Builder()
                .setPaymentAppearance(PaymentAppearance(themeColor = customColor))
                .build()
        )

        val themeColor = AirwallexThemeConfig.themeColor

        assertEquals(ComposeColor(customColor), themeColor)
    }

    @Test
    fun `test programmatic setThemeColor works when no PaymentAppearance`() {
        val customColor = ComposeColor.Red
        AirwallexPlugins.initialize(AirwallexConfiguration.Builder().build())
        AirwallexThemeConfig.setThemeColor(customColor)

        val themeColor = AirwallexThemeConfig.themeColor

        assertEquals(customColor, themeColor)
    }

    @Test
    fun `test PaymentAppearance theme color overrides programmatic config`() {
        val programmaticColor = ComposeColor.Red
        val appearanceColor = Color.parseColor("#612FFF")

        AirwallexThemeConfig.setThemeColor(programmaticColor)
        AirwallexPlugins.initialize(
            AirwallexConfiguration.Builder()
                .setPaymentAppearance(PaymentAppearance(themeColor = appearanceColor))
                .build()
        )

        val themeColor = AirwallexThemeConfig.themeColor

        // PaymentAppearance should override programmatic config
        assertEquals(ComposeColor(appearanceColor), themeColor)
    }

    @Test
    fun `test setThemeColor with hex string`() {
        AirwallexPlugins.initialize(AirwallexConfiguration.Builder().build())

        val result = AirwallexThemeConfig.setThemeColor("#612FFF")

        assertTrue(result)
        assertEquals(ComposeColor(0xFF612FFF), AirwallexThemeConfig.themeColor)
    }

    @Test
    fun `test setThemeColor with invalid hex string returns false`() {
        AirwallexPlugins.initialize(AirwallexConfiguration.Builder().build())

        val result = AirwallexThemeConfig.setThemeColor("invalid")

        assertFalse(result)
    }

    @Test
    fun `test setThemeColor with 0x prefix`() {
        AirwallexPlugins.initialize(AirwallexConfiguration.Builder().build())

        val result = AirwallexThemeConfig.setThemeColor("0x612FFF")

        assertTrue(result)
        assertEquals(ComposeColor(0xFF612FFF), AirwallexThemeConfig.themeColor)
    }

    @Test
    fun `test setThemeColor with ARGB format`() {
        AirwallexPlugins.initialize(AirwallexConfiguration.Builder().build())

        val result = AirwallexThemeConfig.setThemeColor("#FF612FFF")

        assertTrue(result)
        assertEquals(ComposeColor(0xFF612FFF), AirwallexThemeConfig.themeColor)
    }

    @Test
    fun `test PaymentAppearance with both theme color and dark mode`() {
        val customColor = Color.parseColor("#DA8C21")
        AirwallexPlugins.initialize(
            AirwallexConfiguration.Builder()
                .setPaymentAppearance(
                    PaymentAppearance(
                        themeColor = customColor,
                        isDarkTheme = true
                    )
                )
                .build()
        )

        assertTrue(AirwallexThemeConfig.isDarkTheme)
        assertEquals(ComposeColor(customColor), AirwallexThemeConfig.themeColor)
    }

    @Test
    fun `test theme color priority - PaymentAppearance highest`() {
        val appearanceColor = Color.parseColor("#FF0000")
        val programmaticColor = ComposeColor.Green

        // Set programmatic color first
        AirwallexThemeConfig.setThemeColor(programmaticColor)

        // Initialize with PaymentAppearance
        AirwallexPlugins.initialize(
            AirwallexConfiguration.Builder()
                .setPaymentAppearance(PaymentAppearance(themeColor = appearanceColor))
                .build()
        )

        // PaymentAppearance should win
        assertEquals(ComposeColor(appearanceColor), AirwallexThemeConfig.themeColor)
    }

    @Test
    fun `test dark theme priority - PaymentAppearance highest`() {
        AirwallexThemeConfig.setDarkMode(DarkMode.DARK) // Programmatic

        // Initialize with PaymentAppearance forcing light
        AirwallexPlugins.initialize(
            AirwallexConfiguration.Builder()
                .setPaymentAppearance(PaymentAppearance(isDarkTheme = false))
                .build()
        )

        // PaymentAppearance should win over programmatic
        assertFalse(AirwallexThemeConfig.isDarkTheme)
    }

    @Test
    fun `test null PaymentAppearance falls back to programmatic config`() {
        val programmaticColor = ComposeColor.Blue

        AirwallexThemeConfig.setThemeColor(programmaticColor)
        AirwallexPlugins.initialize(
            AirwallexConfiguration.Builder()
                .setPaymentAppearance(null)
                .build()
        )

        assertEquals(programmaticColor, AirwallexThemeConfig.themeColor)
    }
}
