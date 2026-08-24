package com.airwallex.android.view.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.airwallex.android.ui.extension.localizedForAirwallex
import java.util.Locale

@Composable
internal fun AirwallexLocalizedContent(
    locale: Locale?,
    content: @Composable () -> Unit
) {
    val hostContext = LocalContext.current
    val localizedContext = remember(hostContext, locale) {
        hostContext.localizedForAirwallex(locale)
    }

    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalConfiguration provides localizedContext.resources.configuration,
        content = content
    )
}
