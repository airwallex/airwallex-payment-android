package com.airwallex.paymentacceptance.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.airwallex.paymentacceptance.R

class DropdownView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var selectedTextView: SelectedView
    private var dropdownPopup: PopupWindow? = null
    private var options = listOf("One-off payment", "Recurring", "Recurring and Payment")
    private var callback: ((String) -> Unit)? = null
    var currentOption: String = options[0]

    init {
        orientation = VERTICAL
        setBackgroundResource(android.R.color.transparent)
        setWillNotDraw(false)
        selectedTextView = SelectedView(context).apply {
            setOnClickListener { showDropdown() }
        }
        addView(selectedTextView)
    }

    private fun showDropdown() {
        selectedTextView.post {
            val inflater = LayoutInflater.from(context)
            val dropdownView = inflater.inflate(R.layout.dropdown_item, null, false) as LinearLayout
            dropdownPopup = PopupWindow(
                dropdownView,
                selectedTextView.measuredWidth,
                LayoutParams.WRAP_CONTENT,
                true
            ).apply {
                setBackgroundDrawable(context.getDrawable(R.color.color_white))
                val container = LinearLayout(context)
                container.orientation = LinearLayout.VERTICAL
                options.forEach { option ->
                    currentOption = option
                    val itemView = inflater.inflate(R.layout.dropdown_item, container, false)
                    val textView = itemView.findViewById<TextView>(R.id.itemText)
                    textView.text = option
                    textView.setOnClickListener {
                        selectedTextView.setSelectedText(option)
                        dismiss()
                        callback?.invoke(option)
                    }
                    container.addView(itemView)
                }
                contentView = container
                width = selectedTextView.width
                showAsDropDown(this@DropdownView)
            }
        }
    }

    fun setOnOptionSelectedCallback(callback: (String) -> Unit) {
        this.callback = callback
    }
}