package com.airwallex.paymentacceptance.ui.widget

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.airwallex.paymentacceptance.R

class HeaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val backButton: FrameLayout
    private val titleTextView: TextView
    private val settingsButton: ImageView

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.view_header, this, true)

        backButton = findViewById(R.id.flArrow)
        titleTextView = findViewById(R.id.headerTitle)
        settingsButton = findViewById(R.id.headerSettings)

        val a = context.obtainStyledAttributes(attrs, R.styleable.HeaderView, defStyleAttr, 0)
        setTitle(a.getString(R.styleable.HeaderView_titleText) ?: "")
        setSettingsVisible(a.getBoolean(R.styleable.HeaderView_showSettings, false))
        a.recycle()

        backButton.setOnClickListener { hostActivity()?.finish() }
    }

    fun setTitle(text: String) {
        titleTextView.text = text
    }

    fun setSettingsVisible(visible: Boolean) {
        settingsButton.visibility = if (visible) VISIBLE else GONE
    }

    fun setOnSettingsClickListener(listener: () -> Unit) {
        setSettingsVisible(true)
        settingsButton.setOnClickListener { listener.invoke() }
    }

    fun setOnBackClickListener(listener: () -> Unit) {
        backButton.setOnClickListener { listener.invoke() }
    }

    private fun hostActivity(): Activity? {
        var context: Context? = context
        while (context is ContextWrapper) {
            if (context is Activity) return context
            context = context.baseContext
        }
        return null
    }
}
