package com.airwallex.android.ui.composables

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun AirwallexTheme(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    AirwallexThemeConfig.initializeContext(context)

    MaterialTheme(
        colorScheme = createColorScheme(),
        content = content
    )
}

/**
 * Creates a ColorScheme using AirwallexColor semantic properties.
 * Assumes AirwallexThemeConfig is already set with the desired theme color and dark mode.
 */
@Suppress("ComplexMethod")
private fun createColorScheme(): ColorScheme {
    val isDark = AirwallexThemeConfig.isDarkTheme
    val primaryColor = AirwallexColor.theme

    return ColorScheme(
        primary = primaryColor,
        onPrimary = if (isDark) AirwallexColor.Gray90 else AirwallexColor.White,
        primaryContainer = if (isDark) AirwallexColor.Gray90 else AirwallexColor.White,
        onPrimaryContainer = primaryColor,
        inversePrimary = primaryColor,
        secondary = if (isDark) AirwallexColor.Gray50 else AirwallexColor.textSecondary,
        onSecondary = if (isDark) AirwallexColor.Gray90 else AirwallexColor.White,
        secondaryContainer = AirwallexColor.backgroundSecondary,
        onSecondaryContainer = primaryColor,
        tertiary = if (isDark) AirwallexColor.Gray80 else AirwallexColor.White,
        onTertiary = primaryColor,
        tertiaryContainer = AirwallexColor.borderDecorative,
        onTertiaryContainer = primaryColor,
        background = AirwallexColor.backgroundPrimary,
        onBackground = if (isDark) AirwallexColor.Gray10 else primaryColor,
        surface = AirwallexColor.backgroundPrimary,
        onSurface = AirwallexColor.iconSecondary,
        surfaceVariant = primaryColor,
        onSurfaceVariant = if (isDark) AirwallexColor.Gray30 else primaryColor,
        surfaceTint = primaryColor,
        inverseSurface = if (isDark) AirwallexColor.Gray20 else AirwallexColor.Gray90,
        inverseOnSurface = AirwallexColor.backgroundPrimary,
        error = AirwallexColor.borderError,
        onError = AirwallexColor.White,
        errorContainer = if (isDark) AirwallexColor.backgroundHighlight else primaryColor,
        onErrorContainer = if (isDark) AirwallexColor.Red40 else primaryColor,
        outline = if (isDark) AirwallexColor.Gray60 else AirwallexColor.Gray30,
        outlineVariant = if (isDark) AirwallexColor.Gray80 else AirwallexColor.Gray10,
        scrim = if (isDark) AirwallexColor.Black else AirwallexColor.Gray50,
        surfaceBright = if (isDark) AirwallexColor.Gray70 else primaryColor,
        surfaceContainer = AirwallexColor.backgroundHighlight,
        surfaceContainerHighest = if (isDark) AirwallexColor.Gray80 else AirwallexColor.White,
        surfaceContainerHigh = if (isDark) AirwallexColor.Gray90 else AirwallexColor.White,
        surfaceContainerLow = AirwallexColor.backgroundPrimary,
        surfaceContainerLowest = if (isDark) AirwallexColor.Black else AirwallexColor.White,
        surfaceDim = AirwallexColor.backgroundPrimary
    )
}