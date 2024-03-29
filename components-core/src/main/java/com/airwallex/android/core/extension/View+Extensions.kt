package com.airwallex.android.core.extension

import android.view.View

fun View.setOnSingleClickListener(listener: View.OnClickListener) {
    setOnClickListener(OnSingleClickListener(listener))
}

class OnSingleClickListener : View.OnClickListener {
    private val listener: View.OnClickListener
    private var prevTime = 0L

    constructor(listener: View.OnClickListener) {
        this.listener = listener
    }

    constructor(listener: (View) -> Unit) {
        this.listener = View.OnClickListener { listener.invoke(it) }
    }

    companion object {
        private const val DELAY = 1000L
    }

    override fun onClick(v: View?) {
        val time = System.currentTimeMillis()
        if (time >= prevTime + DELAY) {
            prevTime = time
            listener.onClick(v)
        }
    }
}
