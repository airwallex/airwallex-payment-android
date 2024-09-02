package com.airwallex.android.card.view.widget

import android.content.Context
import android.util.AttributeSet
import com.airwallex.android.card.R
import com.airwallex.android.core.util.CardUtils

import com.airwallex.android.ui.widget.AirwallexTextInputLayout
import com.airwallex.android.ui.widget.ValidatedInput

/**
 * A [AirwallexTextInputLayout] to format the credit card cvc, display errors and support callback interface
 */
internal class CvcTextInputLayout constructor(
    context: Context,
    attrs: AttributeSet?
) : AirwallexTextInputLayout(context, attrs, R.layout.cvc_input_layout), ValidatedInput {

    /**
     * Check if cvc is valid
     */
    override val isValid: Boolean
        get() = (teInput as CvcEditText).isValid

    override val emptyErrorMessage: String
        get() = resources.getString(R.string.airwallex_card_empty_cvc)

    override val invalidErrorMessage: String
        get() = resources.getString(R.string.airwallex_card_invalid_cvc)

    /**
     * Return the cvc value based on user input
     */
    internal val cvcValue: String?
        get() = (teInput as CvcEditText).cvcValue

    fun setCardNumber(cardNumber: String?) {
        val brand = CardUtils.getPossibleCardBrand(cardNumber = cardNumber, shouldNormalize = true)
        (teInput as CvcEditText).setCardBrand(brand)
    }
}
