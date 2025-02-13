package com.airwallex.android.card.view.widget

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.view.View
import com.airwallex.android.card.R
import com.airwallex.android.core.CardBrand

import com.airwallex.android.core.util.BuildHelper
import com.google.android.material.textfield.TextInputEditText

/**
 * A [TextInputEditText] to format the credit card cvc
 */
class CvcEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.editTextStyle
) : TextInputEditText(context, attrs, defStyleAttr) {

    private var validCvcLength = 3

    /**
     * Return the cvc value if valid, otherwise null.
     */
    internal val cvcValue: String?
        get() {
            return rawCvcValue.takeIf { isValid }
        }

    private val rawCvcValue: String
        get() {
            return text.toString().trim()
        }

    /**
     * Check if cvc is valid
     */
    internal val isValid: Boolean
        get() {
            return validCvcLength == rawCvcValue.length
        }

    init {
        setHint(R.string.airwallex_card_cvc_hint)
        maxLines = 1
        filters = arrayOf(InputFilter.LengthFilter(validCvcLength))

        inputType = InputType.TYPE_CLASS_NUMBER

        if (BuildHelper.isVersionAtLeastO()) {
            setAutofillHints(View.AUTOFILL_HINT_CREDIT_CARD_SECURITY_CODE)
        }
    }

    fun setCardBrand(brand: CardBrand) {
        validCvcLength = if (brand == CardBrand.Amex) 4 else 3
        filters = arrayOf(InputFilter.LengthFilter(validCvcLength))
    }
}
