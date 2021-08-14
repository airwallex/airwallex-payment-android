package com.airwallex.android.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import com.airwallex.android.R

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
        tlInput.errorIconDrawable = null
        input.errorCallback = { showError ->
            if (showError) {
                error = resources.getString(R.string.airwallex_invalid_card_number)
                tlInput.error = " "
            } else {
                error = null
                tlInput.error = null
            }
        }

        input.brandChangeCallback = { brand ->
            findViewById<ImageView>(R.id.ivBrand).setImageResource(brand.icon)
        }
    }
}
