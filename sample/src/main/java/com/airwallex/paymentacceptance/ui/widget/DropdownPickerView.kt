package com.airwallex.paymentacceptance.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.withStyledAttributes
import com.airwallex.paymentacceptance.R

class DropdownPickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var selectedTextView: DropdownLabelView
    private var dropdownPopup: PopupWindow? = null
    private var options = emptyList<String>()
    private var callback: ((String) -> Unit)? = null
    var currentOption: String = ""

    init {
        orientation = VERTICAL
        setBackgroundResource(android.R.color.transparent)
        setWillNotDraw(false)
        selectedTextView = DropdownLabelView(context).apply {
            setOnClickListener { showDropdown() }
        }
        addView(selectedTextView)

        context.withStyledAttributes(attrs, R.styleable.DropdownSelectView) {
            val titleText = getString(R.styleable.DropdownSelectView_titleContent)
            titleText?.let {
                selectedTextView.setTitleText(it)
            }
        }
    }

    fun setOptions(options: List<String>) {
        this.options = options
        currentOption = options[0]
        selectedTextView.setSelectedText(currentOption)
    }

    fun setTitleText(text: String) {
        selectedTextView.setTitleText(text)
    }

    fun setSelectOption(option: String) {
        selectedTextView.setSelectedText(option)
        currentOption = option
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
                    val itemView = inflater.inflate(R.layout.dropdown_item, container, false)
                    val textView = itemView.findViewById<TextView>(R.id.itemText)
                    textView.text = option
                    textView.setOnClickListener {
                        selectedTextView.setSelectedText(option)
                        dismiss()
                        currentOption = option
                        callback?.invoke(option)
                    }
                    container.addView(itemView)
                }
                contentView = container
                width = selectedTextView.width
                showAsDropDown(this@DropdownPickerView)
            }
        }
    }

    fun setOnOptionSelectedCallback(callback: (String) -> Unit) {
        this.callback = callback
    }
}