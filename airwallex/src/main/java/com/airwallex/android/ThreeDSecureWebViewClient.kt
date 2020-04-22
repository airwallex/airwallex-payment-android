package com.airwallex.android

import android.webkit.WebView

class ThreeDSecureWebViewClient(private val callbacks: Callbacks) :
    AirwallexWebViewClient(callbacks) {

    override fun hasCallbackUrl(view: WebView?, url: String?): Boolean {
        Logger.debug(TAG, "Loading Url: $url")
        if (url?.contains(ThreeDSecure.THREE_DS_RETURN_URL) == true) {
            val payload = url.replace(ThreeDSecure.THREE_DS_RETURN_URL, "")
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
