package com.airwallex.android.view

internal interface ValidatedInput {
    val isValid: Boolean
    val emptyErrorMessage: String
    val invalidErrorMessage: String

    fun listenFocusChanged() {
        if (this is AirwallexTextInputLayout) {
            afterFocusChanged { hasFocus ->
                error = if (!hasFocus) {
                    when {
                        value.isEmpty() -> emptyErrorMessage
                        !isValid -> invalidErrorMessage
                        else -> null
                    }
                } else {
                    null
                }
            }
        }

    }
}