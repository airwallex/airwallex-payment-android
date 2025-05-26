package com.airwallex.android.view.composables.common

sealed interface FocusState {
    object Focused : FocusState
    object Unfocused : FocusState
    object Initial : FocusState
}