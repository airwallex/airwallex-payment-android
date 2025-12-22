package com.airwallex.android.threedsecurity

import android.graphics.Bitmap
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.threedsecurity.exception.WebViewConnectionException
import com.airwallex.android.ui.AirwallexWebView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.net.URLDecoder

class ThreeDSecureWebViewClient(private val callbacks: Callbacks) :
    AirwallexWebViewClient(callbacks) {

    private val mainScope: CoroutineScope = MainScope()
    private var es6DetectionPending = true

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

        // Only inject ES6 detection once
        if (es6DetectionPending && view is AirwallexWebView) {
            es6DetectionPending = false
            injectES6Detection(view)
        }
    }

    /**
     * Injects JavaScript to proactively detect ES6 support
     * This is more reliable than parsing console errors
     */
    private fun injectES6Detection(webView: AirwallexWebView) {
        // Add JavaScript interface to receive detection results
        webView.addJavascriptInterface(ES6DetectionInterface(), ES6_DETECTOR_INTERFACE)

        // Inject JavaScript that tests core ES6 features
        val es6DetectionScript = """
            (function() {
                try {
                    // Test 1: Arrow functions
                    eval('(() => {})');

                    // Test 2: const and let
                    eval('const x = 1; let y = 2;');

                    // Test 3: Template literals
                    eval('`template`');

                    // Test 4: Destructuring
                    eval('const {a} = {a: 1};');

                    // Test 5: Spread operator
                    eval('const arr = [...[1, 2]];');

                    // Test 6: Default parameters
                    eval('function f(a = 1) {}');

                    // All tests passed - ES6 is supported
                    window.ES6Detector.onDetectionComplete(true, 'ES6 supported');
                } catch (e) {
                    // ES6 is NOT supported
                    window.ES6Detector.onDetectionComplete(false, 'ES6 error: ' + e.message);
                }
            })();
        """.trimIndent()

        // Execute the detection script
        webView.evaluateJavascript(es6DetectionScript, null)
    }

    /**
     * JavaScript interface to receive ES6 detection results
     * Note: This is called from JavaScript thread, so we need to post to main thread
     */
    inner class ES6DetectionInterface {
        @JavascriptInterface
        fun onDetectionComplete(isSupported: Boolean, message: String) {
            AirwallexLogger.info("ES6 detection completed: supported=$isSupported, message=$message")

            if (!isSupported) {
                AirwallexLogger.error("WebView does not support ES6: $message")
                // Post to main thread to ensure proper callback handling
                mainScope.launch {
                    callbacks.onWebViewError(
                        WebViewConnectionException("WebView does not support ES6 features required for 3DS.")
                    )
                }
            }
        }
    }

    /**
     * Cleanup method to cancel pending coroutines
     * Should be called when the WebViewClient is no longer needed
     */
    fun cleanup() {
        mainScope.cancel()
    }

    interface Callbacks : WebViewClientCallbacks {
        fun onWebViewConfirmation(payload: String)
    }

    companion object {
        private const val ACS_RESPONSE = "acsResponse"
        private const val ES6_DETECTOR_INTERFACE = "ES6Detector"
    }
}
