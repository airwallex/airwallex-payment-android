package com.airwallex.android.ui.composables

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle

@Composable
fun ScreenView(onStart: () -> Unit) {
    LifecycleEffect(
        event = Lifecycle.Event.ON_START,
    ) {
        onStart()
    }
}