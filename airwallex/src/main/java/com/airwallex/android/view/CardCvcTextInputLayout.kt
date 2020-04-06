package com.airwallex.android.view

import android.content.Context
import android.util.AttributeSet
import com.airwallex.android.R
import com.google.android.material.textfield.TextInputEditText

/**
 * A [AirwallexTextInputLayout] to format the credit card cvc, display errors and support callback interface
 */
internal class CardCvcTextInputLayout constructor(
    context: Context,
    attrs: AttributeSet
) : AirwallexTextInputLayout(context, attrs, R.layout.card_cvc_input_layout) {

    /**
     * Check if cvc is valid
     */
    internal val isValid: Boolean
        get() = (teInput as CardCvcEditText).isValid

    /**
     * Return the cvc value
     */
    internal val cvcValue: String?
        get() = (teInput as CardCvcEditText).cvcValue
}
