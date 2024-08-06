package com.airwallex.android.view.inputs

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import com.airwallex.android.R
import com.airwallex.android.ui.widget.AirwallexTextInputLayout
import com.airwallex.android.ui.widget.ValidatedInput

internal class CardNameTextInputLayout constructor(
    context: Context,
    attrs: AttributeSet?
) : AirwallexTextInputLayout(context, attrs), ValidatedInput {
    override val isValid: Boolean
        get() = value.isNotEmpty()

    override val emptyErrorMessage: String
        get() = resources.getString(R.string.airwallex_empty_card_name)

    override val invalidErrorMessage: String
        get() = emptyErrorMessage

    init {
        setHint(resources.getString(R.string.airwallex_card_name_hint))
        teInput.inputType = InputType.TYPE_CLASS_TEXT
    }
}