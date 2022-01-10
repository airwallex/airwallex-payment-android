package com.airwallex.android.card

import android.webkit.WebView
import com.airwallex.android.card.exception.WebViewConnectionException
import com.airwallex.android.core.log.Logger

class ThreeDSecureWebViewClient(private val callbacks: Callbacks) :
    AirwallexWebViewClient(callbacks) {

    override fun hasCallbackUrl(view: WebView?, url: String?): Boolean {
        Logger.debug(TAG, "Redirect Url: $url")
        // Intercept paRes and return
        if (url?.contains(ACS_RESPONSE) == true) {
            val subUrl = url.substring(url.indexOf(ACS_RESPONSE) + ACS_RESPONSE.length + 1)
            val payload = if (subUrl.contains("&")) {
                subUrl.substring(0, subUrl.indexOf("&"))
            } else {
                subUrl
            }
            if (payload.isNotEmpty()) {
                callbacks.onWebViewConfirmation(payload)
            } else {
                callbacks.onWebViewError(WebViewConnectionException("3DS failed. No acsResponse were obtained."))
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
        const val ACS_RESPONSE = "acsResponse"
    }
}
