package com.airwallex.android.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun LifecycleEffect(
    event: Lifecycle.Event = Lifecycle.Event.ON_ANY,
    skipCount: Int = 0,
    onEvent: (Lifecycle.Event) -> Unit
) {
    val eventHandler = rememberUpdatedState(onEvent)
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        var eventCounter = 0
        val observer = LifecycleEventObserver { _, actualEvent ->
            if (event == Lifecycle.Event.ON_ANY || event == actualEvent) {
                if (eventCounter < skipCount) {
                    eventCounter += 1
                } else {
                    eventHandler.value(actualEvent)
                }
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}