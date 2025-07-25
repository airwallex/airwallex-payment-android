package com.airwallex.android.ui.composables

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.colorResource
import com.airwallex.android.ui.R
import com.airwallex.android.ui.composables.AirwallexColor.adjustByLevel

@Composable
fun AirwallexTheme(content: @Composable () -> Unit) {
    val tintColor = colorResource(R.color.airwallex_tint_color)
    val materialColorScheme = remember(tintColor) {
        ColorScheme(
            primary = tintColor,
            onPrimary = AirwallexColor.White,
            primaryContainer = AirwallexColor.White,
            onPrimaryContainer = tintColor,
            inversePrimary = tintColor,
            secondary = AirwallexColor.TextSecondary,
            onSecondary = AirwallexColor.White,
            secondaryContainer = AirwallexColor.Gray10,
            onSecondaryContainer = tintColor,
            tertiary = AirwallexColor.White,
            onTertiary = tintColor,
            tertiaryContainer = AirwallexColor.Gray20,
            onTertiaryContainer = tintColor,
            background = AirwallexColor.White,
            onBackground = tintColor,
            surface = AirwallexColor.White,
            onSurface = AirwallexColor.Gray50,
            surfaceVariant = tintColor,
            onSurfaceVariant = tintColor,
            surfaceTint = tintColor,
            inverseSurface = AirwallexColor.Gray90,
            inverseOnSurface = AirwallexColor.White,
            error = AirwallexColor.Red50,
            onError = AirwallexColor.White,
            errorContainer = tintColor,
            onErrorContainer = tintColor,
            outline = AirwallexColor.Gray30,
            outlineVariant = AirwallexColor.Gray10,
            scrim = AirwallexColor.Gray50,
            surfaceBright = tintColor,
            surfaceContainer = tintColor.adjustByLevel(AirwallexColor.Level.Level5),
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