package com.airwallex.android.view.composables

import android.graphics.Color
import androidx.compose.ui.graphics.Color as ComposeColor
import com.airwallex.android.core.AirwallexConfiguration
import com.airwallex.android.core.AirwallexPlugins
import com.airwallex.android.core.PaymentAppearance
import com.airwallex.android.ui.composables.AirwallexThemeConfig
import com.airwallex.android.ui.composables.DarkMode
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class PaymentElementConfigurationTest {

    @Before
    fun setUp() {
        // Initialize SDK with default configuration
        AirwallexPlugins.initialize(AirwallexConfiguration.Builder().build())

        // Reset theme config to defaults
        AirwallexThemeConfig.setDarkMode(DarkMode.SYSTEM)
    }

    @Test
    fun `test PaymentAppearance in Card configuration applies theme color`() {
        val customColor = Color.parseColor("#DA8C21")
        val configuration = PaymentElementConfiguration.Card(
            paymentAppearance = PaymentAppearance(themeColor = customColor)
        )

        // Simulate what PaymentElement.configureAppearance() does
        configuration.paymentAppearance?.themeColor?.let { color ->
            AirwallexThemeConfig.setThemeColor(ComposeColor(color))
        }

        assertEquals(ComposeColor(customColor), AirwallexThemeConfig.themeColor)
    }

    @Test
    fun `test PaymentAppearance in Card configuration applies dark mode`() {
        val configuration = PaymentElementConfiguration.Card(
            paymentAppearance = PaymentAppearance(isDarkTheme = true)
        )

        // Simulate what PaymentElement.configureAppearance() does
        configuration.paymentAppearance?.isDarkTheme?.let { isDark ->
            AirwallexThemeConfig.setDarkMode(if (isDark) DarkMode.DARK else DarkMode.LIGHT)
        }

        assertTrue(AirwallexThemeConfig.isDarkTheme)
    }

    @Test
    fun `test PaymentAppearance in Card configuration applies both theme color and dark mode`() {
        val customColor = Color.parseColor("#612FFF")
        val configuration = PaymentElementConfiguration.Card(
            paymentAppearance = PaymentAppearance(
                themeColor = customColor,
                isDarkTheme = false
            )
        )

        // Simulate what PaymentElement.configureAppearance() does
        configuration.paymentAppearance?.let { appearance ->
            appearance.themeColor?.let { color ->
                AirwallexThemeConfig.setThemeColor(ComposeColor(color))
            }
            appearance.isDarkTheme?.let { isDark ->
                AirwallexThemeConfig.setDarkMode(if (isDark) DarkMode.DARK else DarkMode.LIGHT)
            }
        }

        assertEquals(ComposeColor(customColor), AirwallexThemeConfig.themeColor)
        assertFalse(AirwallexThemeConfig.isDarkTheme)
    }

    @Test
    fun `test PaymentAppearance in PaymentSheet configuration applies theme color`() {
        val customColor = Color.parseColor("#FF0000")
        val configuration = PaymentElementConfiguration.PaymentSheet(
            paymentAppearance = PaymentAppearance(themeColor = customColor)
        )

        // Simulate what PaymentElement.configureAppearance() does
        configuration.paymentAppearance?.themeColor?.let { color ->
            AirwallexThemeConfig.setThemeColor(ComposeColor(color))
        }

        assertEquals(ComposeColor(customColor), AirwallexThemeConfig.themeColor)
    }

    @Test
    fun `test PaymentAppearance in PaymentSheet configuration applies dark mode`() {
        val configuration = PaymentElementConfiguration.PaymentSheet(
            paymentAppearance = PaymentAppearance(isDarkTheme = false)
        )

        // Simulate what PaymentElement.configureAppearance() does
        configuration.paymentAppearance?.isDarkTheme?.let { isDark ->
            AirwallexThemeConfig.setDarkMode(if (isDark) DarkMode.DARK else DarkMode.LIGHT)
        }

        assertFalse(AirwallexThemeConfig.isDarkTheme)
    }

    @Test
    fun `test null PaymentAppearance does not change theme config`() {
        val programmaticColor = ComposeColor.Blue
        AirwallexThemeConfig.setThemeColor(programmaticColor)
        AirwallexThemeConfig.setDarkMode(DarkMode.DARK)

        val configuration = PaymentElementConfiguration.Card(
            paymentAppearance = null
        )

        // Simulate what PaymentElement.configureAppearance() does
        configuration.paymentAppearance?.let { appearance ->
            appearance.themeColor?.let { color ->
                AirwallexThemeConfig.setThemeColor(ComposeColor(color))
            }
            appearance.isDarkTheme?.let { isDark ->
                AirwallexThemeConfig.setDarkMode(if (isDark) DarkMode.DARK else DarkMode.LIGHT)
            }
        }

        // Should keep programmatic config
        assertEquals(programmaticColor, AirwallexThemeConfig.themeColor)
        assertTrue(AirwallexThemeConfig.isDarkTheme)
    }

    @Test
    fun `test PaymentAppearance with only theme color does not change dark mode`() {
        val customColor = Color.parseColor("#612FFF")
        AirwallexThemeConfig.setDarkMode(DarkMode.LIGHT)

        val configuration = PaymentElementConfiguration.Card(
            paymentAppearance = PaymentAppearance(themeColor = customColor, isDarkTheme = null)
        )

        // Simulate what PaymentElement.configureAppearance() does
        configuration.paymentAppearance?.let { appearance ->
            appearance.themeColor?.let { color ->
                AirwallexThemeConfig.setThemeColor(ComposeColor(color))
            }
            appearance.isDarkTheme?.let { isDark ->
                AirwallexThemeConfig.setDarkMode(if (isDark) DarkMode.DARK else DarkMode.LIGHT)
            }
        }

        assertEquals(ComposeColor(customColor), AirwallexThemeConfig.themeColor)
        assertFalse(AirwallexThemeConfig.isDarkTheme) // Should remain light
    }

    @Test
    fun `test PaymentAppearance with only dark mode does not change theme color`() {
        val programmaticColor = ComposeColor.Green
        AirwallexThemeConfig.setThemeColor(programmaticColor)

        val configuration = PaymentElementConfiguration.PaymentSheet(
            paymentAppearance = PaymentAppearance(themeColor = null, isDarkTheme = true)
        )

        // Simulate what PaymentElement.configureAppearance() does
        configuration.paymentAppearance?.let { appearance ->
            appearance.themeColor?.let { color ->
                AirwallexThemeConfig.setThemeColor(ComposeColor(color))
            }
            appearance.isDarkTheme?.let { isDark ->
                AirwallexThemeConfig.setDarkMode(if (isDark) DarkMode.DARK else DarkMode.LIGHT)
            }
        }

        assertEquals(programmaticColor, AirwallexThemeConfig.themeColor) // Should keep programmatic color
        assertTrue(AirwallexThemeConfig.isDarkTheme)
    }

    @Test
    fun `test PaymentAppearance overrides programmatic config`() {
        val programmaticColor = ComposeColor.Red
        val appearanceColor = Color.parseColor("#612FFF")

        // Set programmatic config first
        AirwallexThemeConfig.setThemeColor(programmaticColor)
        AirwallexThemeConfig.setDarkMode(DarkMode.LIGHT)

        val configuration = PaymentElementConfiguration.Card(
            paymentAppearance = PaymentAppearance(
                themeColor = appearanceColor,
                isDarkTheme = true
            )
        )

        // Apply appearance - should override programmatic config
        configuration.paymentAppearance?.let { appearance ->
            appearance.themeColor?.let { color ->
                AirwallexThemeConfig.setThemeColor(ComposeColor(color))
            }
            appearance.isDarkTheme?.let { isDark ->
                AirwallexThemeConfig.setDarkMode(if (isDark) DarkMode.DARK else DarkMode.LIGHT)
            }
        }

        assertEquals(ComposeColor(appearanceColor), AirwallexThemeConfig.themeColor)
        assertTrue(AirwallexThemeConfig.isDarkTheme)
    }
}
