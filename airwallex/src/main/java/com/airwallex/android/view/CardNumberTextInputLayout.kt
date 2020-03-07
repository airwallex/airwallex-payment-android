package com.airwallex.android.view

import android.content.Context
import android.util.AttributeSet
import com.airwallex.android.R
import kotlinx.android.synthetic.main.card_number_input_layout.view.*

internal class CardNumberTextInputLayout constructor(
    context: Context,
    attrs: AttributeSet
) : AirwallexTextInputLayout(context, attrs, R.layout.card_number_input_layout) {

    internal var completionCallback: () -> Unit = {}
        set(value) {
            (teInput as CardNumberEditText).completionCallback = value
            field = value
        }

    internal val isValid: Boolean
        get() = (teInput as CardNumberEditText).isCardNumberValid

    internal val cardNumber: String?
        get() = (teInput as CardNumberEditText).cardNumber

    init {
        val input = teInput as CardNumberEditText
        tlInput.errorIconDrawable = null
        input.errorCallback = { showError ->
            if (showError) {
                error = resources.getString(R.string.invalid_card_number)
                tlInput.error = " "
            } else {
                error = null
                tlInput.error = null
            }
        }

        input.brandChangeCallback = { brand ->
            ivBrand.setImageResource(brand.icon)
        }
    }
}
