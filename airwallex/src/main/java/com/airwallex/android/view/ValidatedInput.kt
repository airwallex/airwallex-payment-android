package com.airwallex.android.view

internal interface ValidatedInput {
    val isValid: Boolean
    val emptyErrorMessage: String
    val invalidErrorMessage: String
}