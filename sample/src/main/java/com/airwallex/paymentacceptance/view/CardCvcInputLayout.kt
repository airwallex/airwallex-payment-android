package com.airwallex.paymentacceptance.view

import android.content.Context
import android.util.AttributeSet
import com.airwallex.paymentacceptance.R

internal class CardCvcTextInputLayout constructor(
    context: Context,
    attrs: AttributeSet
) : InputLayout(context, attrs, R.layout.card_cvc_input_layout) {

    internal var completionCallback: () -> Unit = {}
        set(value) {
            (teInput as CardCvcEditText).completionCallback = value
            field = value
        }

    internal val isValid: Boolean
        get() = (teInput as CardCvcEditText).isValid

    internal val cvcValue: String?
        get() = (teInput as CardCvcEditText).cvcValue
}