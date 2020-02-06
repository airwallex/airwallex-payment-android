package com.airwallex.paymentacceptance.view

import android.content.Context
import android.util.AttributeSet
import com.airwallex.android.model.PaymentMethod.Card.CardBrand.Companion.MASTERCARD
import com.airwallex.android.model.PaymentMethod.Card.CardBrand.Companion.VISA
import com.airwallex.paymentacceptance.R
import kotlinx.android.synthetic.main.card_number_input_layout.view.*

internal class CardNumberTextInputLayout constructor(
    context: Context,
    attrs: AttributeSet
) : InputLayout(context, attrs, R.layout.card_number_input_layout) {

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
        input.errorCallback = { showError ->
            error = if (showError) {
                resources.getString(R.string.invalid_card_number)
            } else {
                null
            }
        }

        input.brandChangeCallback = { brand ->
            when (brand) {
                VISA -> ivBrand.setImageResource(R.drawable.airwallex_ic_visa)
                MASTERCARD -> ivBrand.setImageResource(R.drawable.airwallex_ic_mastercard)
                else -> ivBrand.setImageResource(R.drawable.airwallex_ic_card_default)
            }
        }
    }
}