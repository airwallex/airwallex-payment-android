package com.airwallex.android.view

import android.content.Context
import android.os.Build
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.view.View
import com.airwallex.android.R
import android.support.design.widget.TextInputEditText

/**
 * A [TextInputEditText] to format the credit card cvc
 */
internal class CardCvcEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.editTextStyle
) : TextInputEditText(context, attrs, defStyleAttr) {

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
            return rawCvcValue.length == VALID_CVC_LENGTH
        }

    init {
        setHint(R.string.cvc_hint)
        maxLines = 1
        filters = arrayOf(InputFilter.LengthFilter(VALID_CVC_LENGTH))

        inputType = InputType.TYPE_CLASS_NUMBER

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setAutofillHints(View.AUTOFILL_HINT_CREDIT_CARD_SECURITY_CODE)
        }
    }

    companion object {
        const val VALID_CVC_LENGTH = 3
    }
}
