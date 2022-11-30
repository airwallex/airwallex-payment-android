package com.airwallex.android.view.inputs

internal interface ValidatedInput {
    val isValid: Boolean
    val emptyErrorMessage: String
    val invalidErrorMessage: String
}