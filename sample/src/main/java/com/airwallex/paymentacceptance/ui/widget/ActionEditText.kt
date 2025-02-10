package com.airwallex.paymentacceptance.ui.widget

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.withStyledAttributes
import com.airwallex.paymentacceptance.R

class ActionEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var editText: EditText
    private var actionText: TextView
    private var deleteIcon: View

    init {
        LayoutInflater.from(context).inflate(R.layout.view_action_edit_text, this, true)

        editText = findViewById(R.id.editText)
        actionText = findViewById(R.id.actionText)
        deleteIcon = findViewById(R.id.deleteIcon)

        context.withStyledAttributes(attrs, R.styleable.ActionEditText) {
            val hint = getString(R.styleable.ActionEditText_etHint)
            val actionLabel = getString(R.styleable.ActionEditText_actionLabel)

            editText.hint = hint
            if (actionLabel.isNullOrEmpty()) {
                actionText.visibility = GONE
            } else {
                actionText.text = actionLabel
                actionText.visibility = VISIBLE
            }
        }


        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                toggleAction(s?.isNotEmpty() == true)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        deleteIcon.setOnClickListener {
            editText.text.clear()
        }
    }

    private fun toggleAction(showDelete: Boolean) {
        if (showDelete) {
            actionText.visibility = GONE
            deleteIcon.visibility = VISIBLE
        } else {
            actionText.visibility = VISIBLE
            deleteIcon.visibility = GONE
        }
    }

    fun setActionClickListener(listener: OnClickListener) {
        actionText.setOnClickListener(listener)
    }

    fun setText(text: String) {
        editText.setText(text)
    }

    fun getText(): String {
        return editText.text.toString()
    }

    fun setDisable() {
        editText.isEnabled = false
    }
}