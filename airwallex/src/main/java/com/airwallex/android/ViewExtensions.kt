package com.airwallex.android

import android.view.View

fun View.setOnSingleClickListener(listener: View.OnClickListener) {
    setOnClickListener(OnSingleClickListener(listener))
}
