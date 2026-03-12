package com.airwallex.android.ui.util

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.widget.TextViewCompat

/**
 * Utility functions for applying dynamic colors to EditText and TextView
 */
object EditTextColorUtil {

    /**
     * Sets the cursor and text selection handle colors for an EditText
     * @param textView The TextView/EditText to apply colors to
     * @param cursorColor The color for the cursor
     * @param context The context for dimension conversion
     */
    fun applyCursorColor(
        textView: TextView,
        cursorColor: Color,
        context: Context
    ) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            val cursorDrawable = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(cursorColor.toArgb(), cursorColor.toArgb())
            ).apply {
                setSize(2.dpToPx(context), 0)
            }
            textView.textCursorDrawable = cursorDrawable

            textView.highlightColor = cursorColor.copy(alpha = 0.3f).toArgb()
            textView.textSelectHandleLeft?.setTint(cursorColor.toArgb())
            textView.textSelectHandleRight?.setTint(cursorColor.toArgb())
            textView.textSelectHandle?.setTint(cursorColor.toArgb())
        } else {
            try {
                val cursorDrawable = GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT,
                    intArrayOf(cursorColor.toArgb(), cursorColor.toArgb())
                ).apply {
                    setSize(2.dpToPx(context), 0)
                }
                val field = TextView::class.java.getDeclaredField("mCursorDrawableRes")
                field.isAccessible = true
                field.set(textView, 0)

                val editorField = TextView::class.java.getDeclaredField("mEditor")
                editorField.isAccessible = true
                val editor = editorField.get(textView)
                val cursorDrawableField = editor.javaClass.getDeclaredField("mCursorDrawable")
                cursorDrawableField.isAccessible = true
                cursorDrawableField.set(editor, arrayOf(cursorDrawable, cursorDrawable))
            } catch (_: Exception) {
                TextViewCompat.setCompoundDrawableTintList(
                    textView,
                    ColorStateList.valueOf(cursorColor.toArgb())
                )
            }

            textView.highlightColor = cursorColor.copy(alpha = 0.3f).toArgb()
        }
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}
