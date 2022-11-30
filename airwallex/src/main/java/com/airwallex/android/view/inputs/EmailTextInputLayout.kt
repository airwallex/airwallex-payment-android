package com.airwallex.android.view.inputs

import android.content.Context
import android.util.AttributeSet
import android.util.Patterns
import com.airwallex.android.R
import com.airwallex.android.view.inputs.ValidatedInput

internal class EmailTextInputLayout constructor(
    context: Context,
    attrs: AttributeSet?
) : AirwallexTextInputLayout(context, attrs), ValidatedInput {
    override val isValid: Boolean
        get() = Patterns.EMAIL_ADDRESS.matcher(value).matches()

    override val emptyErrorMessage: String
        get() = resources.getString(R.string.airwallex_empty_email)

    override val invalidErrorMessage: String
        get() = resources.getString(R.string.airwallex_invalid_email)

    init {
        setHint(resources.getString(R.string.airwallex_email_hint))
    }
}