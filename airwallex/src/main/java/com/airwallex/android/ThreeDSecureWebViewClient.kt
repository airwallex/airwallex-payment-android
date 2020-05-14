package com.airwallex.android

import android.webkit.WebView

internal class ThreeDSecureWebViewClient(private val callbacks: Callbacks) :
    AirwallexWebViewClient(callbacks) {

    override fun hasCallbackUrl(view: WebView?, url: String?): Boolean {
        Logger.debug(TAG, "Loading Url: $url")
        if (url?.contains("example") == true) {
            val payload = url.replace("example", "")
            callbacks.onWebViewConfirmation(payload)
            return true
        }
        return false
    }

    interface Callbacks : WebViewClientCallbacks {
        fun onWebViewConfirmation(payload: String)
    }

    companion object {
        const val TAG = "ThreeDSecureWebViewClient"
    }
}
