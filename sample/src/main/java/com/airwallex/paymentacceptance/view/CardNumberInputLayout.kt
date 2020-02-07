package com.airwallex.paymentacceptance.view

import android.content.Context
import android.util.AttributeSet
import com.airwallex.paymentacceptance.R
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

    val isValid: Boolean
        get() = (teInput as CardNumberEditText).isCardNumberValid

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