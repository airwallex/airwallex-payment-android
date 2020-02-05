package com.airwallex.paymentacceptance.view

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import com.airwallex.paymentacceptance.R
import kotlinx.android.synthetic.main.view_text_input.view.*

internal class AirwallexTextInputLayout constructor(
    context: Context,
    attrs: AttributeSet
) : LinearLayout(context, attrs) {

    var error: String?
        set(value) {
            tvError.visibility = when (value) {
                null -> View.GONE
                else -> View.VISIBLE
            }

            tvError.text = value
            updateLayoutColor()
        }
        get() {
            return tvError.text.toString()
        }

    var text: String
        get() {
            return teInput.text.toString()
        }
        set(value) {
            teInput.setText(value)
        }

    init {
        View.inflate(getContext(), R.layout.view_text_input, this)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.AirwallexTextInputLayout, 0, 0
        ).apply {
            try {
                if (hasValue(R.styleable.AirwallexTextInputLayout_hint)) {
                    tlInput.hint = getString(R.styleable.AirwallexTextInputLayout_hint)
                }

                if (hasValue(R.styleable.AirwallexTextInputLayout_text)) {
                    teInput.setText(getString(R.styleable.AirwallexTextInputLayout_text))
                }

                if (hasValue(R.styleable.AirwallexTextInputLayout_android_imeOptions)) {
                    teInput.imeOptions =
                        getInt(
                            R.styleable.AirwallexTextInputLayout_android_imeOptions,
                            EditorInfo.IME_NULL
                        )
                }

                if (hasValue(R.styleable.AirwallexTextInputLayout_android_inputType)) {
                    teInput.inputType =
                        getInt(
                            R.styleable.AirwallexTextInputLayout_android_inputType,
                            EditorInfo.TYPE_NULL
                        )
                }
            } finally {
                recycle()
            }
        }
    }

    private fun updateLayoutColor() {
        if (error.isNullOrEmpty()) {
            vBorder.background =
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.bg_input_layout_border,
                    null
                )
//            teInput.setHintTextColor(ContextCompat.getColor(context, R.color.colorEditTextAccent))
        } else {
            vBorder.background =
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.bg_input_layout_border_error,
                    null
                )
//            teInput.setHintTextColor(ContextCompat.getColor(context, R.color.colorEditTextError))
        }
    }

    fun requestInputFocus() {
        teInput.requestFocus()
    }

    fun afterTextChanged(afterTextChanged: (String) -> Unit) {
        teInput.afterTextChanged(afterTextChanged)
    }

    fun afterFocusChanged(afterFocusChanged: (Boolean) -> Unit) {
        teInput.setOnFocusChangeListener { _, hasFocus ->
            afterFocusChanged.invoke(hasFocus)
        }
    }

    fun setOnEditorActionListener(l: (actionId: Int, event: KeyEvent?) -> Boolean) {
        teInput.setOnEditorActionListener { _, actionId, event ->
            l.invoke(actionId, event)
        }
    }
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}