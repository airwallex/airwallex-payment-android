package com.airwallex.paymentacceptance.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.withStyledAttributes
import com.airwallex.paymentacceptance.R

class DropdownPickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var selectedTextView: DropdownLabelView
    private var listPopup: ListPopupWindow? = null
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

        context.withStyledAttributes(attrs, R.styleable.DropdownPickerView) {
            val titleText = getString(R.styleable.DropdownPickerView_titleContent)
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
            listPopup?.dismiss()

            val adapter = ArrayAdapter(
                context,
                R.layout.dropdown_item,
                R.id.itemText,
                options,
            )

            listPopup = ListPopupWindow(context).apply {
                anchorView = this@DropdownPickerView
                setAdapter(adapter)
                width = selectedTextView.width
                isModal = true
                setDropDownGravity(Gravity.START)

                setOnItemClickListener { _, _, position, _ ->
                    val option = options[position]
                    selectedTextView.setSelectedText(option)
                    currentOption = option
                    callback?.invoke(option)
                    dismiss()
                }

                show()
            }
        }
    }

    fun setOnOptionSelectedCallback(callback: (String) -> Unit) {
        this.callback = callback
    }

    override fun onDetachedFromWindow() {
        listPopup?.dismiss()
        listPopup = null
        super.onDetachedFromWindow()
    }
}
