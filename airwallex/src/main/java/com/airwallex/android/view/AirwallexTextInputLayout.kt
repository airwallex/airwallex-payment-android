package com.airwallex.android.view

import android.content.Context
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.content.res.ResourcesCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.airwallex.android.R
import com.airwallex.android.R.styleable

internal open class AirwallexTextInputLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    resourceLayout: Int = R.layout.common_text_input_layout
) : LinearLayout(context, attrs) {

    private var tlInput: TextInputLayout
    var teInput: TextInputEditText
    private var vBorder: View
    private var tvError: TextView

    internal var error: String?
        set(value) {
            when (value) {
                null -> {
                    tvError.visibility = View.GONE
                }
                else -> {
                    tvError.visibility = View.VISIBLE
                }
            }
            tvError.text = value
            updateLayoutColor()
        }
        get() {
            return tvError.text.toString()
        }

    internal var value: String
        get() {
            return teInput.text?.trim().toString()
        }
        set(value) {
            teInput.setText(value)
            val layoutParams = teInput.layoutParams as FrameLayout.LayoutParams
            if (value.isBlank()) {
                layoutParams.bottomMargin = resources.getDimension(R.dimen.airwallex_te_input_bottom).toInt()
            } else {
                layoutParams.bottomMargin = 0
            }
            teInput.layoutParams = layoutParams
        }

    init {
        View.inflate(getContext(), resourceLayout, this)

        tlInput = findViewById(R.id.tlInput)
        teInput = findViewById(R.id.teInput)
        vBorder = findViewById(R.id.vBorder)
        tvError = findViewById(R.id.tvError)

        context.theme.obtainStyledAttributes(
            attrs,
            styleable.AirwallexTextInputLayout, 0, 0
        ).apply {
            try {
                if (hasValue(styleable.AirwallexTextInputLayout_hint)) {
                    tlInput.hint = getString(styleable.AirwallexTextInputLayout_hint)
                }

                if (hasValue(styleable.AirwallexTextInputLayout_text)) {
                    teInput.setText(getString(styleable.AirwallexTextInputLayout_text))
                }

                if (hasValue(styleable.AirwallexTextInputLayout_android_imeOptions)) {
                    teInput.imeOptions =
                        getInt(
                            styleable.AirwallexTextInputLayout_android_imeOptions,
                            EditorInfo.IME_NULL
                        )
                }

                if (hasValue(styleable.AirwallexTextInputLayout_android_inputType)) {
                    teInput.inputType =
                        getInt(
                            styleable.AirwallexTextInputLayout_android_inputType,
                            EditorInfo.TYPE_NULL
                        )
                }
            } finally {
                recycle()
            }
        }

        val layoutParams = teInput.layoutParams as FrameLayout.LayoutParams
        layoutParams.bottomMargin = resources.getDimension(R.dimen.airwallex_te_input_bottom).toInt()
        teInput.layoutParams = layoutParams
    }

    private fun updateLayoutColor() {
        if (error.isNullOrEmpty()) {
            vBorder.background =
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.airwallex_input_layout_border,
                    null
                )
        } else {
            vBorder.background =
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.airwallex_input_layout_border_error,
                    null
                )
        }
    }

    internal fun requestInputFocus() {
        teInput.requestFocus()
    }

    internal fun afterTextChanged(afterTextChanged: (String) -> Unit) {
        teInput.afterTextChanged(afterTextChanged)
    }

    internal fun afterFocusChanged(afterFocusChanged: (Boolean) -> Unit) {
        teInput.setOnFocusChangeListener { _, hasFocus ->
            afterFocusChanged.invoke(hasFocus)

            val layoutParams = teInput.layoutParams as FrameLayout.LayoutParams
            if (hasFocus) {
                layoutParams.bottomMargin = 0
            } else {
                if (teInput.text.isNullOrBlank()) {
                    layoutParams.bottomMargin = resources.getDimension(R.dimen.airwallex_te_input_bottom).toInt()
                } else {
                    layoutParams.bottomMargin = 0
                }
            }
            teInput.layoutParams = layoutParams
        }
    }

    internal fun setOnEditorActionListener(l: (actionId: Int, event: KeyEvent?) -> Boolean) {
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
