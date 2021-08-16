package com.airwallex.android.card

import android.view.ViewGroup
import android.webkit.WebView

fun WebView.destroyWebView() {
    // Make sure you remove the WebView from its parent view before doing anything.
    val viewGroup = this.parent as ViewGroup
    viewGroup.removeAllViews()

    this.clearHistory()

    // NOTE: clears RAM cache, if you pass true, it will also clear the disk cache.
    // Probably not a great idea to pass true if you have other WebViews still alive.
    this.clearCache(true)

    // Loading a blank page is optional, but will ensure that the WebView isn't doing anything when you destroy it.
    this.loadUrl("about:blank")

    this.onPause()
    this.removeAllViews()
    @Suppress("DEPRECATION")
    this.destroyDrawingCache()

    // NOTE: This can occasionally cause a segfault below API 17 (4.2)
    this.destroy()
}
