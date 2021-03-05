package com.airwallex.android

import android.content.Context
import android.support.annotation.AttrRes
import android.support.annotation.ColorInt
import android.util.TypedValue

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
