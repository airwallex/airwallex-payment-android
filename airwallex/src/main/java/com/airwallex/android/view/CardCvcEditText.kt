package com.airwallex.android.view

import android.content.Context
import android.os.Build
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.view.View
import com.airwallex.android.R
import com.google.android.material.textfield.TextInputEditText

internal class CardCvcEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.editTextStyle
) : TextInputEditText(context, attrs, defStyleAttr) {

    companion object {
        const val CVC_LENGTH = 3
    }

    internal val cvcValue: String?
        get() {
            return rawCvcValue.takeIf { isValid }
        }

    private val rawCvcValue: String
        get() {
            return text.toString().trim()
        }

    internal val isValid: Boolean
        get() {
            return rawCvcValue.length == CVC_LENGTH
        }

    init {
        setHint(R.string.cvc_hint)
        maxLines = 1
        filters = arrayOf(InputFilter.LengthFilter(CVC_LENGTH))

        inputType = InputType.TYPE_CLASS_NUMBER

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setAutofillHints(View.AUTOFILL_HINT_CREDIT_CARD_SECURITY_CODE)
        }
    }
}
