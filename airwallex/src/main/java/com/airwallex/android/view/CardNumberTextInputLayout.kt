package com.airwallex.android.view

import android.content.Context
import android.util.AttributeSet
import com.airwallex.android.R
import kotlinx.android.synthetic.main.card_number_input_layout.view.*

/**
 * A [AirwallexTextInputLayout] to format the credit card number, display errors and support callback interface
 */
internal class CardNumberTextInputLayout constructor(
    context: Context,
    attrs: AttributeSet
) : AirwallexTextInputLayout(context, attrs, R.layout.card_number_input_layout) {

    /**
     * Callback of complete input credit card number
     */
    internal var completionCallback: () -> Unit = {}
        set(value) {
            (teInput as CardNumberEditText).completionCallback = value
            field = value
        }

    /**
     * Check if credit card number is valid
     */
    internal val isValid: Boolean
        get() = (teInput as CardNumberEditText).isCardNumberValid

    /**
     * Return the credit card number
     */
    internal val cardNumber: String?
        get() = (teInput as CardNumberEditText).cardNumber

    init {
        val input = teInput as CardNumberEditText
        input.errorCallback = { showError ->
            error = if (showError) {
                resources.getString(R.string.invalid_card_number)
            } else {
                null
            }
        }

        input.brandChangeCallback = { brand ->
            ivBrand.setImageResource(brand.icon)
        }
    }
}
