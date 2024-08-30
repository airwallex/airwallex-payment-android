package com.airwallex.android.view.inputs

import android.content.Context
import android.util.AttributeSet
import com.airwallex.android.R
import com.airwallex.android.ui.widget.AirwallexTextInputLayout
import com.airwallex.android.core.CardBrand

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

    internal var brandChangeCallback: (CardBrand) -> Unit = {}

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
        setPlaceHolder(resources.getString(R.string.airwallex_card_number_placeholder))
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
            brandChangeCallback(brand)
        }

        afterFocusChanged { hasFocus ->
            if (hasFocus) {
                error = null
                setHint(resources.getString(R.string.airwallex_card_number_label))
            } else {
                error = validationMessageCallback(value)
                if (value.isEmpty()) {
                    setHint(null)
                }
            }
        }
    }
}
