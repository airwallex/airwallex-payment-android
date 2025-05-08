package com.airwallex.android.ui.composables

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun AirwallexTheme(content: @Composable () -> Unit) {
    val materialColorScheme = remember {
        ColorScheme(
            primary = AirwallexColor.Ultraviolet70,
            onPrimary = AirwallexColor.White,
            primaryContainer = AirwallexColor.White,
            onPrimaryContainer = AirwallexColor.Ultraviolet70,
            inversePrimary = AirwallexColor.Ultraviolet70,
            secondary = AirwallexColor.TextSecondary,
            onSecondary = AirwallexColor.White,
            secondaryContainer = AirwallexColor.Gray10,
            onSecondaryContainer = AirwallexColor.Ultraviolet70,
            tertiary = AirwallexColor.White,
            onTertiary = AirwallexColor.Ultraviolet70,
            tertiaryContainer = AirwallexColor.Gray20,
            onTertiaryContainer = AirwallexColor.Ultraviolet70,
            background = AirwallexColor.White,
            onBackground = AirwallexColor.Ultraviolet70,
            surface = AirwallexColor.White,
            onSurface = AirwallexColor.Gray50,
            surfaceVariant = AirwallexColor.Ultraviolet70,
            onSurfaceVariant = AirwallexColor.Ultraviolet70,
            surfaceTint = AirwallexColor.Ultraviolet70,
            inverseSurface = AirwallexColor.Gray90,
            inverseOnSurface = AirwallexColor.White,
            error = AirwallexColor.Red50,
            onError = AirwallexColor.White,
            errorContainer = AirwallexColor.Ultraviolet70,
            onErrorContainer = AirwallexColor.Ultraviolet70,
            outline = AirwallexColor.Gray30,
            outlineVariant = AirwallexColor.Gray10,
            scrim = AirwallexColor.Gray50,
            surfaceBright = AirwallexColor.Ultraviolet70,
            surfaceContainer = AirwallexColor.White,
            surfaceContainerHighest = AirwallexColor.White,
            surfaceContainerHigh = AirwallexColor.White,
            surfaceContainerLow = AirwallexColor.White,
            surfaceContainerLowest = AirwallexColor.White,
            surfaceDim = AirwallexColor.White
        )
    }

    MaterialTheme(
        colorScheme = materialColorScheme,
        content = content
    )
}