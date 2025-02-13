package com.airwallex.paymentacceptance.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import com.airwallex.paymentacceptance.R

class SwitchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val label: TextView
    private val switchControl: SwitchCompat

    init {
        orientation = HORIZONTAL
        LayoutInflater.from(context).inflate(R.layout.view_switch, this, true)

        label = findViewById(R.id.label)
        switchControl = findViewById(R.id.switchControl)

        context.theme.obtainStyledAttributes(attrs, R.styleable.SwitchView, 0, 0).apply {
            try {
                val labelText = getString(R.styleable.SwitchView_labelText)
                setLabelText(labelText ?: "")
            } finally {
                recycle()
            }
        }
    }

    fun setLabelText(text: String) {
        label.text = text
    }

    fun setOnCheckedChangeListener(listener: (Boolean) -> Unit) {
        switchControl.setOnCheckedChangeListener { _, isChecked -> listener(isChecked) }
    }

    fun isChecked(): Boolean {
        return switchControl.isChecked
    }

    fun setChecked(checked: Boolean) {
        switchControl.isChecked = checked
    }
}