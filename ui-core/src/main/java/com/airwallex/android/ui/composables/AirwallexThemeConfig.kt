package com.airwallex.android.ui.composables

import android.content.Context
import android.content.res.Configuration
import androidx.compose.ui.graphics.Color

/**
 * Dark mode configuration for Airwallex SDK UI.
 */
enum class DarkMode {
    /** Force light mode regardless of system setting */
    LIGHT,

    /** Force dark mode regardless of system setting */
    DARK,

    /** Follow system dark mode setting (default) */
    SYSTEM
}

/**
 * Global theme configuration for Airwallex SDK UI.
 * Configure theme using setThemeColor() and setDarkMode().
 * Context is initialized lazily when AirwallexTheme is first rendered.
 */
object AirwallexThemeConfig {
    private var configuredDarkMode: DarkMode = DarkMode.SYSTEM
    private var configuredThemeColor: Color = AirwallexColor.Ultraviolet70
    private var applicationContext: Context? = null

    /**
     * Current dark theme state. Resolves SYSTEM mode to actual boolean.
     */
    val isDarkTheme: Boolean
        get() = when (configuredDarkMode) {
            DarkMode.LIGHT -> false
            DarkMode.DARK -> true
            DarkMode.SYSTEM -> {
                // Get from application context if available, otherwise default to false
                applicationContext?.isSystemInDarkMode() ?: false
            }
        }

    val themeColor: Color
        get() = configuredThemeColor

    /**
     * Initialize context for system dark mode detection.
     * Called lazily when AirwallexTheme is first rendered.
     *
     * @param context Application or Activity context
     */
    internal fun initializeContext(context: Context) {
        if (applicationContext == null) {
            applicationContext = context.applicationContext
        }
    }

    /**
     * Set dark mode preference.
     *
     * @param darkMode Dark mode preference (LIGHT, DARK, or SYSTEM)
     */
    fun setDarkMode(darkMode: DarkMode) {
        this.configuredDarkMode = darkMode
    }

    fun setThemeColor(color: Color) {
        this.configuredThemeColor = color
    }

    /**
     * Set theme color using hex color string.
     * Supported formats: "#RRGGBB", "#AARRGGBB", "0xRRGGBB", "0xAARRGGBB"
     *
     * @param colorString Hex color string
     * @return true if color was parsed successfully, false otherwise
     */
    fun setThemeColor(colorString: String): Boolean {
        val color = parseColorString(colorString)
        return if (color != null) {
            this.configuredThemeColor = color
            true
        } else {
            false
        }
    }

    /**
     * Parse hex color string to Compose Color.
     * Supports: "#RRGGBB", "#AARRGGBB", "0xRRGGBB", "0xAARRGGBB"
     */
    private fun parseColorString(colorString: String): Color? {
        return try {
            val hex = when {
                colorString.startsWith("#") -> colorString.substring(1)
                colorString.startsWith("0x", ignoreCase = true) ||
                        colorString.startsWith("0X", ignoreCase = true) -> colorString.substring(2)

                else -> colorString
            }.trim()

            // Validate hex string length
            if (hex.length != 6 && hex.length != 8) {
                return null
            }

            // Parse hex string and convert to ARGB Int
            val colorLong = hex.toLong(16)

            // Add alpha FF if it's 6 digits (RRGGBB)
            val argb = if (hex.length == 6) {
                (0xFF000000L or colorLong).toInt()
            } else {
                colorLong.toInt()
            }

            // Use Color(Int) constructor which expects ARGB format
            Color(argb)
        } catch (e: Exception) {
            // Log error for debugging
            android.util.Log.e("AirwallexThemeConfig", "Failed to parse color: $colorString", e)
            null
        }
    }

    private fun Context.isSystemInDarkMode(): Boolean {
        return (
                resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                ) == Configuration.UI_MODE_NIGHT_YES
    }
}
