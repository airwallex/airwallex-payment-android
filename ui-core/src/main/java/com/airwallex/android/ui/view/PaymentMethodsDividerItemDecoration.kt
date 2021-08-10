package com.airwallex.android.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.airwallex.android.ui.R

internal class PaymentMethodsDividerItemDecoration(val context: Context, resId: Int, val availableThirdPaymentTypeSize: Int) : ItemDecoration() {
    private var divider: Drawable? = ContextCompat.getDrawable(context, resId)

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = context.resources.getDimension(R.dimen.divider_padding)
        val right = parent.width - context.resources.getDimension(R.dimen.divider_padding)
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            // No divider for space
            if (availableThirdPaymentTypeSize > 0 && i == availableThirdPaymentTypeSize) {
                continue
            }
            val child: View = parent.getChildAt(i)
            val params =
                child.layoutParams as RecyclerView.LayoutParams
            val top: Int = child.bottom + params.bottomMargin
            val bottom = top + (divider?.intrinsicHeight ?: 0)
            divider?.setBounds(left.toInt(), top, right.toInt(), bottom)
            divider?.draw(c)
        }
    }
}
