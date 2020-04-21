package com.airwallex.android

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.webkit.WebSettings
import android.webkit.WebView

internal class AirwallexWebView @SuppressLint("SetJavaScriptEnabled") constructor(
    context: Context?,
    attrs: AttributeSet?
) : WebView(context, attrs) {
    constructor(context: Context?) : this(context, null)

    init {
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.setSupportMultipleWindows(true)
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        settings.setSupportMultipleWindows(true)
        settings.defaultTextEncodingName = "utf-8"
    }
}
