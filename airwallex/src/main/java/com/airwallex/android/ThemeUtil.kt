package com.airwallex.android

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt

internal object ThemeUtil {
    @ColorInt
    fun getPrimaryThemeColor(context: Context): Int {
        return getAttributeColor(context, R.attr.colorPrimary)
    }

    @ColorInt
    private fun getAttributeColor(context: Context, @AttrRes attributeColor: Int): Int {
        val typedValue = TypedValue()
        val typedArray = context.obtainStyledAttributes(typedValue.data, intArrayOf(attributeColor))
        val color = typedArray.getColor(0, 0)
        typedArray.recycle()
        return color
    }
}
