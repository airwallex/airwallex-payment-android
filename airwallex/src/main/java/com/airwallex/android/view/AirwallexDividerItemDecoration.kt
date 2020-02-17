package com.airwallex.android.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.airwallex.android.R

class AirwallexDividerItemDecoration : ItemDecoration {
    private var divider: Drawable?
    private var context: Context? = null

    constructor(context: Context) {
        val styledAttributes =
            context.obtainStyledAttributes(ATTRS)
        divider = styledAttributes.getDrawable(0)
        styledAttributes.recycle()
    }

    constructor(context: Context, resId: Int) {
        this.context = context
        divider = ContextCompat.getDrawable(context, resId)
    }

    override fun onDraw(
        c: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val padding =
            context?.resources?.getDimension(R.dimen.airwallex_divider_padding)?.toInt() ?: 0
        val left = parent.paddingLeft + padding
        val right = parent.width - parent.paddingRight - padding
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params =
                child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + divider!!.intrinsicHeight
            divider!!.setBounds(left, top, right, bottom)
            divider!!.draw(c)
        }
    }

    companion object {
        private val ATTRS = intArrayOf(android.R.attr.listDivider)
    }
}