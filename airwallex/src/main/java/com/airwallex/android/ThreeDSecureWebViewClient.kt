package com.airwallex.android

import android.net.Uri
import android.webkit.WebView
import com.airwallex.android.exception.WebViewConnectionException

internal class ThreeDSecureWebViewClient(private val callbacks: Callbacks) :
    AirwallexWebViewClient(callbacks) {

    override fun hasCallbackUrl(view: WebView?, url: String?): Boolean {
        Logger.debug(TAG, "Redirect Url: $url")
        // Intercept paRes and return
        if (url?.contains(REDIRECT_PARES_URL_SCHEMA) == true) {
            val payload = Uri.parse(url).getQueryParameter("PaRes")
            if (payload != null) {
                callbacks.onWebViewConfirmation(payload)
            } else {
                callbacks.onWebViewError(WebViewConnectionException("3DS failed. No PaRes were obtained"))
            }
            return true
        }
        return false
    }

    interface Callbacks : WebViewClientCallbacks {
        fun onWebViewConfirmation(payload: String)
    }

    companion object {
        const val TAG = "ThreeDSecureWebViewClient"

        const val REDIRECT_PARES_URL_SCHEMA = "/feedback/success"
    }
}
