package com.airwallex.paymentacceptance.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.withStyledAttributes
import com.airwallex.paymentacceptance.R

class LabeledDropdown @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val titleText: TextView
    private val selectedText: TextView
    private val box: View
    private var listPopup: ListPopupWindow? = null
    private var options = emptyList<String>()
    private var callback: ((String) -> Unit)? = null
    var currentOption: String = ""

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.view_labeled_dropdown, this, true)

        titleText = findViewById(R.id.titleText)
        selectedText = findViewById(R.id.selectedText)
        box = findViewById(R.id.box)
        box.setOnClickListener { showDropdown() }

        context.withStyledAttributes(attrs, R.styleable.LabeledDropdown) {
            setTitle(getString(R.styleable.LabeledDropdown_title))
        }
    }

    fun setTitle(text: String?) {
        if (text.isNullOrBlank()) {
            titleText.visibility = GONE
        } else {
            titleText.text = text
            titleText.visibility = VISIBLE
        }
    }

    fun setOptions(options: List<String>) {
        this.options = options
        val first = options.firstOrNull() ?: return
        currentOption = first
        selectedText.text = first
    }

    fun setSelectOption(option: String) {
        selectedText.text = option
        currentOption = option
    }

    fun setOnOptionSelectedCallback(callback: (String) -> Unit) {
        this.callback = callback
    }

    private fun showDropdown() {
        box.post {
            listPopup?.dismiss()

            val adapter = ArrayAdapter(
                context,
                R.layout.dropdown_item,
                R.id.itemText,
                options,
            )

            listPopup = ListPopupWindow(context).apply {
                anchorView = box
                setAdapter(adapter)
                width = box.width
                isModal = true
                setDropDownGravity(Gravity.START)

                setOnItemClickListener { _, _, position, _ ->
                    val option = options[position]
                    setSelectOption(option)
                    callback?.invoke(option)
                    dismiss()
                }

                show()
            }
        }
    }

    override fun onDetachedFromWindow() {
        listPopup?.dismiss()
        listPopup = null
        super.onDetachedFromWindow()
    }
}
