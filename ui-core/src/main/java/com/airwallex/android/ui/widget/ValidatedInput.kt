package com.airwallex.android.ui.widget

interface ValidatedInput {
    val isValid: Boolean
    val emptyErrorMessage: String
    val invalidErrorMessage: String
}