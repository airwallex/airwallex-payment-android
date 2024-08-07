package com.airwallex.android.threedsecurity

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.os.Build
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.util.BuildHelper
import com.airwallex.android.threedsecurity.exception.WebViewConnectionException

abstract class AirwallexWebViewClient(private val callbacks: WebViewClientCallbacks) :
    WebViewClient() {

    abstract fun hasCallbackUrl(view: WebView?, url: String?): Boolean

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val url = request?.url.toString()
        return if (hasCallbackUrl(view, url)) {
            true
        } else !url.startsWith(HTTP)
    }

    @Deprecated("Deprecated in API level 23")
    override fun onReceivedError(
        view: WebView,
        errorCode: Int,
        description: String,
        failingUrl: String
    ) {
        if (BuildHelper.isVersionAtLeastM()) {
            return
        }
        callbacks.onWebViewError(WebViewConnectionException("$errorCode, $description"))
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onReceivedError(
        view: WebView,
        request: WebResourceRequest,
        error: WebResourceError
    ) {
        // Please be aware that the new SDK 23 callback will be called for any resource
        // (iframe, image, etc) that failed to load, not just for the main page
        if (request.isForMainFrame) {
            callbacks.onWebViewError(
                WebViewConnectionException(
                    error.errorCode.toString() + ", " + error.description.toString()
                )
            )
        }
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        AirwallexLogger.debug("onPageFinished $url")
        callbacks.onPageFinished(url)
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        AirwallexLogger.debug("onPageStarted $url")
        callbacks.onPageStarted(url)
    }

    interface WebViewClientCallbacks {
        fun onWebViewError(error: WebViewConnectionException)
        fun onPageFinished(url: String?)
        fun onPageStarted(url: String?)
    }

    companion object {
        const val HTTP = "http"
    }
}
