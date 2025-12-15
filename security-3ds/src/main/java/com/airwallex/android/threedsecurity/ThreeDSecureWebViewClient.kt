package com.airwallex.android.threedsecurity

import android.graphics.Bitmap
import android.webkit.WebView
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.threedsecurity.exception.WebViewConnectionException
import com.airwallex.android.ui.AirwallexWebView
import java.net.URLDecoder

class ThreeDSecureWebViewClient(private val callbacks: Callbacks) :
    AirwallexWebViewClient(callbacks) {

    override fun hasCallbackUrl(view: WebView?, url: String?): Boolean {
        AirwallexLogger.info("ThreeDSecureWebViewClient hasCallbackUrl: acsResponse = ${url?.contains(ACS_RESPONSE)}", sensitiveMessage = "Redirect Url = $url")
        // Intercept paRes and return
        if (url?.contains(ACS_RESPONSE) == true) {
            val subUrl = url.substring(url.indexOf(ACS_RESPONSE) + ACS_RESPONSE.length + 1)
            val payload = if (subUrl.contains("&")) {
                subUrl.substringBefore("&")
            } else {
                subUrl
            }
            if (payload.isNotEmpty()) {
                callbacks.onWebViewConfirmation(URLDecoder.decode(payload, "UTF-8"))
            } else {
                callbacks.onWebViewError(WebViewConnectionException("3DS failed. No acsResponse were obtained."))
            }
            return true
        }
        return false
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)

        // Set up console error monitoring for ES6 syntax errors
        if (view is AirwallexWebView) {
            view.consoleErrorCallback = { message, source, line ->
                detectES6ErrorsInConsole(message, source, line)
            }
        }
    }

    /**
     * Monitor console errors for ES6-related syntax errors
     */
    private fun detectES6ErrorsInConsole(message: String, source: String?, line: Int) {
        val es6ErrorPatterns = listOf(
            "unexpected token",
            "unexpected identifier",
            "unexpected reserved word",
            "const",
            "let",
            "=>",
            "SyntaxError",
            "arrow function",
            "template literal",
            "spread operator"
        )

        val messageLC = message.lowercase()
        val isES6Error = es6ErrorPatterns.any { pattern ->
            messageLC.contains(pattern.lowercase())
        }

        if (isES6Error) {
            AirwallexLogger.error("ES6 syntax error detected in console: $message at $source:$line")
            callbacks.onWebViewError(
                WebViewConnectionException("WebView does not support ES6. ES6 syntax error detected: $message")
            )
        }
    }

    interface Callbacks : WebViewClientCallbacks {
        fun onWebViewConfirmation(payload: String)
    }

    companion object {
        const val TAG = "ThreeDSecureWebViewClient"
        const val ACS_RESPONSE = "acsResponse"
    }
}
