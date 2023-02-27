package com.airwallex.android.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.airwallex.android.databinding.WarningViewBinding

class WarningView(context: Context, attrs: AttributeSet?) :
    LinearLayout(context, attrs) {
    private val viewBinding = WarningViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    var message: String? = null
        set(value) {
            viewBinding.title.text = value
            field = value
        }
}