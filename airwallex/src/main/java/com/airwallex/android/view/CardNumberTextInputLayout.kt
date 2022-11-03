package com.airwallex.android.view

import android.content.Context
import android.util.AttributeSet
import com.airwallex.android.R

/**
 * A [AirwallexTextInputLayout] to format the credit card number, display errors and support callback interface
 */
internal class CardNumberTextInputLayout constructor(
    context: Context,
    attrs: AttributeSet?
) : AirwallexTextInputLayout(context, attrs, R.layout.card_number_input_layout) {

    /**
     * Callback of complete input credit card number
     */
    internal var completionCallback: () -> Unit = {}
        set(value) {
            (teInput as CardNumberEditText).completionCallback = value
            field = value
        }

    internal var validationMessageCallback: (String) -> String? = { null }
        set(value) {
            (teInput as CardNumberEditText).validationMessageCallback = value
            field = value
        }

    /**
     * Check if credit card number is valid
     */
    internal val isValid: Boolean
        get() = (teInput as CardNumberEditText).validationMessage == null

    /**
     * Return the card number
     */
    internal val cardNumber: String?
        get() = (teInput as CardNumberEditText).cardNumber

    init {
        val input = teInput as CardNumberEditText
        tlInput.errorIconDrawable = null
        input.errorCallback = { errorMessage ->
            if (errorMessage != null) {
                error = errorMessage
            } else {
                error = null
                tlInput.error = null
            }
        }

        input.brandChangeCallback = { brand ->
            teInput.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                brand.icon,
                0
            )
        }

        afterFocusChanged { hasFocus ->
            if (hasFocus) {
                error = null
                teInput.setHint(R.string.airwallex_card_number_placeholder)
            } else {
                error = validationMessageCallback(value)
                teInput.hint = null
            }
        }
    }
}
