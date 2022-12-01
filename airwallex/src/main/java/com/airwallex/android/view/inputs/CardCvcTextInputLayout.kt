package com.airwallex.android.view.inputs

import android.content.Context
import android.util.AttributeSet
import com.airwallex.android.R

/**
 * A [AirwallexTextInputLayout] to format the credit card cvc, display errors and support callback interface
 */
internal class CardCvcTextInputLayout constructor(
    context: Context,
    attrs: AttributeSet?
) : AirwallexTextInputLayout(context, attrs, R.layout.card_cvc_input_layout), ValidatedInput {

    /**
     * Check if cvc is valid
     */
    override val isValid: Boolean
        get() = (teInput as CardCvcEditText).isValid

    override val emptyErrorMessage: String
        get() = resources.getString(R.string.airwallex_empty_cvc)

    override val invalidErrorMessage: String
        get() = resources.getString(R.string.airwallex_invalid_cvc)

    /**
     * Return the cvc value based on user input
     */
    internal val cvcValue: String?
        get() = (teInput as CardCvcEditText).cvcValue
}
