package com.airwallex.paymentacceptance.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.airwallex.paymentacceptance.R

class SelectedView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var titleTextView: TextView
    private var selectedTextView: TextView
    private var arrowIcon: ImageView

    init {
        orientation = VERTICAL

        LayoutInflater.from(context).inflate(R.layout.selected_view, this, true)

        titleTextView = findViewById(R.id.titleText)
        selectedTextView = findViewById(R.id.selectedText)
        arrowIcon = findViewById(R.id.arrowIcon)
    }

    fun setSelectedText(text: String) {
        selectedTextView.text = text
    }
}