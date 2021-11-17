package com.airwallex.android.card

import android.net.Uri
import android.webkit.WebView
import com.airwallex.android.card.exception.WebViewConnectionException
import com.airwallex.android.core.log.Logger

class ThreeDSecureWebViewClient(private val callbacks: Callbacks) :
    AirwallexWebViewClient(callbacks) {

    override fun hasCallbackUrl(view: WebView?, url: String?): Boolean {
        Logger.debug(TAG, "Redirect Url: $url")
        // Intercept paRes and return
        if (url?.contains(PA_RES) == true) {
            val payload = Uri.parse(url).getQueryParameter(PA_RES)
            if (payload != null) {
                callbacks.onWebViewConfirmation(payload)
            } else {
                callbacks.onWebViewError(WebViewConnectionException("3DS failed. No PaRes were obtained."))
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
        const val PA_RES = "paRes"
    }
}
