package com.airwallex.android

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.core.content.ContextCompat

internal class AirwallexWebView @SuppressLint("SetJavaScriptEnabled") constructor(
    context: Context,
    attrs: AttributeSet?
) : WebView(context, attrs) {
    constructor(context: Context) : this(context, null)

    init {
        setBackgroundColor(
            ContextCompat.getColor(
                context,
                R.color.airwallex_color_bg
            )
        )
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.setSupportMultipleWindows(true)
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        settings.defaultTextEncodingName = "UTF-8"
    }
}
