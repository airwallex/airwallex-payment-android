package com.airwallex.android.ui.composables

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.airwallex.android.ui.composables.AirwallexColor.adjustByLevel

@Composable
fun AirwallexTheme(
    customTintColor: Color? = null,
    darkTheme: Boolean? = null, // null = use global config
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    AirwallexThemeConfig.initializeContext(context)

    val tintColor = customTintColor ?: AirwallexThemeConfig.themeColor
    val isDark = darkTheme ?: AirwallexThemeConfig.isDarkTheme

    val colorScheme = remember(tintColor, isDark) {
        if (isDark) {
            createDarkColorScheme(tintColor)
        } else {
            createLightColorScheme(tintColor)
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

/**
 * Creates a light mode ColorScheme with the given tint color.
 */
private fun createLightColorScheme(tintColor: Color): ColorScheme {
    val primaryColor = tintColor.adjustByLevel(AirwallexColor.Level.Level70)

    return ColorScheme(
        primary = primaryColor,
        onPrimary = AirwallexColor.White,
        primaryContainer = AirwallexColor.White,
        onPrimaryContainer = primaryColor,
        inversePrimary = primaryColor,
        secondary = AirwallexColor.TextSecondary,
        onSecondary = AirwallexColor.White,
        secondaryContainer = AirwallexColor.Gray10,
        onSecondaryContainer = primaryColor,
        tertiary = AirwallexColor.White,
        onTertiary = primaryColor,
        tertiaryContainer = AirwallexColor.Gray20,
        onTertiaryContainer = primaryColor,
        background = AirwallexColor.White,
        onBackground = primaryColor,
        surface = AirwallexColor.White,
        onSurface = AirwallexColor.Gray50,
        surfaceVariant = primaryColor,
        onSurfaceVariant = primaryColor,
        surfaceTint = primaryColor,
        inverseSurface = AirwallexColor.Gray90,
        inverseOnSurface = AirwallexColor.White,
        error = AirwallexColor.Red50,
        onError = AirwallexColor.White,
        errorContainer = primaryColor,
        onErrorContainer = primaryColor,
        outline = AirwallexColor.Gray30,
        outlineVariant = AirwallexColor.Gray10,
        scrim = AirwallexColor.Gray50,
        surfaceBright = primaryColor,
        surfaceContainer = primaryColor.adjustByLevel(AirwallexColor.Level.Level5),
        surfaceContainerHighest = AirwallexColor.White,
        surfaceContainerHigh = AirwallexColor.White,
        surfaceContainerLow = AirwallexColor.White,
        surfaceContainerLowest = AirwallexColor.White,
        surfaceDim = AirwallexColor.White
    )

}

/**
 * Creates a dark mode ColorScheme with the given tint color.
 */
private fun createDarkColorScheme(tintColor: Color): ColorScheme {
    val primaryColor = tintColor.adjustByLevel(AirwallexColor.Level.Level40)

    return ColorScheme(
        primary = primaryColor,
        onPrimary = AirwallexColor.Gray90,
        primaryContainer = AirwallexColor.Gray90,
        onPrimaryContainer = primaryColor,
        inversePrimary = primaryColor,
        secondary = AirwallexColor.Gray50,
        onSecondary = AirwallexColor.Gray90,
        secondaryContainer = AirwallexColor.Gray90,
        onSecondaryContainer = primaryColor,
        tertiary = AirwallexColor.Gray80,
        onTertiary = primaryColor,
        tertiaryContainer = AirwallexColor.Gray80,
        onTertiaryContainer = primaryColor,
        background = AirwallexColor.Gray100,
        onBackground = AirwallexColor.Gray10,
        surface = AirwallexColor.Gray100,
        onSurface = AirwallexColor.Gray50,
        surfaceVariant = primaryColor,
        onSurfaceVariant = AirwallexColor.Gray30,
        surfaceTint = primaryColor,
        inverseSurface = AirwallexColor.Gray20,
        inverseOnSurface = AirwallexColor.Gray100,
        error = AirwallexColor.Red60,
        onError = AirwallexColor.White,
        errorContainer = tintColor.adjustByLevel(AirwallexColor.Level.Level90),
        onErrorContainer = AirwallexColor.Red40,
        outline = AirwallexColor.Gray60,
        outlineVariant = AirwallexColor.Gray80,
        scrim = AirwallexColor.Black,
        surfaceBright = AirwallexColor.Gray70,
        surfaceContainer = tintColor.adjustByLevel(AirwallexColor.Level.Level90),
        surfaceContainerHighest = AirwallexColor.Gray80,
        surfaceContainerHigh = AirwallexColor.Gray90,
        surfaceContainerLow = AirwallexColor.Gray100,
        surfaceContainerLowest = AirwallexColor.Black,
        surfaceDim = AirwallexColor.Gray100
    )
}