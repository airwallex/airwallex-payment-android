package com.airwallex.paymentacceptance.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.airwallex.paymentacceptance.R

class TitleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var titleTextView: TextView
    private var rightButton: Button

    init {
        orientation = HORIZONTAL
        LayoutInflater.from(context).inflate(R.layout.view_title, this, true)

        titleTextView = findViewById(R.id.titleText)
        rightButton = findViewById(R.id.resetButton)

        val a = context.obtainStyledAttributes(attrs, R.styleable.TitleView, defStyleAttr, 0)
        val titleText = a.getString(R.styleable.TitleView_titleText) ?: ""
        val buttonText = a.getString(R.styleable.TitleView_buttonText) ?: ""
        val buttonWidth =
            a.getDimension(R.styleable.TitleView_buttonWidth, LayoutParams.WRAP_CONTENT.toFloat())
        val buttonHeight =
            a.getDimension(R.styleable.TitleView_buttonHeight, LayoutParams.WRAP_CONTENT.toFloat())
        val showRightBtn = a.getBoolean(R.styleable.TitleView_showRightBtn, true)

        setTitle(titleText)
        setButtonText(buttonText)

        rightButton.layoutParams = rightButton.layoutParams.apply {
            width = buttonWidth.toInt()
            height = buttonHeight.toInt()
        }
        if (!showRightBtn) {
            rightButton.visibility = GONE
        }
        a.recycle()
    }

    fun setTitle(text: String) {
        titleTextView.text = text
    }

    fun setButtonText(text: String) {
        rightButton.text = text
    }

    fun setOnButtonClickListener(listener: () -> Unit) {
        rightButton.setOnClickListener { listener.invoke() }
    }
}