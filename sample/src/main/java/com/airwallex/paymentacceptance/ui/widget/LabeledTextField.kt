package com.airwallex.paymentacceptance.ui.widget

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.withStyledAttributes
import com.airwallex.paymentacceptance.R

class LabeledTextField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val titleText: TextView
    private val editText: EditText
    private val deleteIcon: FrameLayout
    private val actionText: TextView

    private var fieldClickListener: (() -> Unit)? = null
    private var actionClickListener: (() -> Unit)? = null

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.view_labeled_text_field, this, true)

        titleText = findViewById(R.id.titleText)
        editText = findViewById(R.id.editText)
        deleteIcon = findViewById(R.id.deleteIcon)
        actionText = findViewById(R.id.actionText)

        deleteIcon.setOnClickListener { editText.text = null }
        actionText.setOnClickListener { actionClickListener?.invoke() }

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) = toggleTrailing()
        })

        context.withStyledAttributes(attrs, R.styleable.LabeledTextField) {
            setTitle(getString(R.styleable.LabeledTextField_title))
            editText.hint = getString(R.styleable.LabeledTextField_fieldHint)
            getString(R.styleable.LabeledTextField_actionLabel)?.let { setActionLabel(it) }
            if (getInt(R.styleable.LabeledTextField_fieldInputType, 0) == 1) {
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
        }
        toggleTrailing()
    }

    fun setTitle(text: String?) {
        if (text.isNullOrBlank()) {
            titleText.visibility = GONE
        } else {
            titleText.text = text
            titleText.visibility = VISIBLE
        }
    }

    fun setHint(hint: String?) {
        editText.hint = hint
    }

    fun setActionLabel(label: String?) {
        actionText.text = label
        toggleTrailing()
    }

    fun setText(text: String?) {
        editText.setText(text)
    }

    fun getText(): String = editText.text?.toString().orEmpty()

    fun setOnFieldClickListener(listener: () -> Unit) {
        fieldClickListener = listener
        editText.isFocusable = false
        editText.isClickable = true
        editText.setOnClickListener { fieldClickListener?.invoke() }
    }

    fun setActionClickListener(listener: () -> Unit) {
        actionClickListener = listener
    }

    fun setDisable(disable: Boolean) {
        editText.isEnabled = !disable
    }

    private fun toggleTrailing() {
        val hasAction = actionText.text?.isNotBlank() == true
        val hasText = editText.text?.isNotEmpty() == true && editText.isEnabled
        when {
            hasAction -> {
                actionText.visibility = VISIBLE
                deleteIcon.visibility = GONE
            }
            hasText -> {
                actionText.visibility = GONE
                deleteIcon.visibility = VISIBLE
            }
            else -> {
                actionText.visibility = GONE
                deleteIcon.visibility = GONE
            }
        }
    }
}
