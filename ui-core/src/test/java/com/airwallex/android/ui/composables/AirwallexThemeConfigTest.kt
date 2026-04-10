package com.airwallex.android.ui.composables

import com.airwallex.android.core.AirwallexConfiguration
import com.airwallex.android.core.AirwallexPlugins
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import androidx.compose.ui.graphics.Color as ComposeColor

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
    fun `test default theme color is Ultraviolet70`() {
        AirwallexPlugins.initialize(AirwallexConfiguration.Builder().build())

        val themeColor = AirwallexThemeConfig.themeColor

        assertEquals(AirwallexColor.Ultraviolet70, themeColor)
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
}
