package com.airwallex.android

import android.webkit.WebView

internal class ThreeDSecureWebViewClient(private val callbacks: Callbacks) :
    AirwallexWebViewClient(callbacks) {

    override fun hasCallbackUrl(view: WebView?, url: String?): Boolean {
        Logger.debug(TAG, "Redirect Url: $url")
        // Intercept paRes and return
        if (url?.contains(ThreeDSecure.TERM_URL + "/") == true) {
            val payload = url.replace(ThreeDSecure.TERM_URL + "/", "")
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
