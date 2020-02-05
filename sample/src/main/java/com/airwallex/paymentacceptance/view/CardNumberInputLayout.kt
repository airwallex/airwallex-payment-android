package com.airwallex.paymentacceptance.view

import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import com.airwallex.paymentacceptance.CardUtils
import com.airwallex.paymentacceptance.R
import kotlinx.android.synthetic.main.common_text_input_layout.view.*

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
    }
}