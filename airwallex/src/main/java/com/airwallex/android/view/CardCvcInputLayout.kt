package com.airwallex.android.view

import android.content.Context
import android.util.AttributeSet
import com.airwallex.android.R

class CardCvcTextInputLayout constructor(
    context: Context,
    attrs: AttributeSet
) : AirwallexTextInputLayout(context, attrs, R.layout.card_cvc_input_layout) {

    val isValid: Boolean
        get() = (teInput as CardCvcEditText).isValid

    var completionCallback: () -> Unit = {}
        set(value) {
            (teInput as CardCvcEditText).completionCallback = value
            field = value
        }

    var changedCallback: () -> Unit = {}
        set(value) {
            (teInput as CardCvcEditText).changedCallback = value
            field = value
        }

    val cvcValue: String?
        get() = (teInput as CardCvcEditText).cvcValue
}