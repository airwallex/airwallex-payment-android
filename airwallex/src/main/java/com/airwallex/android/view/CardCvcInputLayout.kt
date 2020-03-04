package com.airwallex.android.view

import android.content.Context
import android.util.AttributeSet
import com.airwallex.android.R

internal class CardCvcTextInputLayout constructor(
    context: Context,
    attrs: AttributeSet
) : AirwallexTextInputLayout(context, attrs, R.layout.card_cvc_input_layout) {

    internal val isValid: Boolean
        get() = (teInput as CardCvcEditText).isValid

    internal var completionCallback: () -> Unit = {}
        set(value) {
            (teInput as CardCvcEditText).completionCallback = value
            field = value
        }

    internal val cvcValue: String?
        get() = (teInput as CardCvcEditText).cvcValue
}