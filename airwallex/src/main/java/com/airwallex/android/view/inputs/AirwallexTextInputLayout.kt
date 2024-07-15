package com.airwallex.android.view.inputs

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.KeyListener
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import com.airwallex.android.R
import com.airwallex.android.R.styleable
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

open class AirwallexTextInputLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    resourceLayout: Int = R.layout.common_text_input_layout
) : LinearLayout(context, attrs) {

    var tlInput: TextInputLayout
    var teInput: TextInputEditText

    private val borderWidthDefault by lazy {
        resources.getDimension(R.dimen.airwallex_input_layout_border_width_default).toInt()
    }

    private val borderWidthFocused by lazy {
        resources.getDimension(R.dimen.airwallex_input_layout_border_width_focused).toInt()
    }

    var error: String? = null
        set(value) {
            field = value
            if (TextUtils.isEmpty(value)) {
                tlInput.isErrorEnabled = false
                tlInput.boxStrokeWidth = borderWidthDefault
            } else {
                tlInput.error = value
                tlInput.boxStrokeWidth = borderWidthFocused
            }
        }

    var value: String
        get() {
            return teInput.text?.trim().toString()
        }
        set(value) {
            teInput.setText(value)
        }

    init {
        View.inflate(getContext(), resourceLayout, this)

        tlInput = findViewById(R.id.tlInput)
        teInput = findViewById(R.id.teInput)

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
    }

    fun setKeyListener(input: KeyListener) {
        teInput.keyListener = input
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setOnInputEditTextClickListener(listener: OnClickListener) {
        teInput.setOnTouchListener { v, event ->
            if (event?.action == MotionEvent.ACTION_UP) {
                listener.onClick(v)
            }
            false
        }
    }

    fun setInputType(type: Int) {
        teInput.inputType = type
    }

    fun setPlaceHolder(hint: CharSequence?) {
        teInput.hint = hint
    }

    fun setHint(hint: CharSequence?) {
        tlInput.hint = hint
    }

    fun setImeOptions(imeOptions: Int) {
        teInput.imeOptions = imeOptions
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
