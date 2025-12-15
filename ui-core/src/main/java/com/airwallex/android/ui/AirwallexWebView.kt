package com.airwallex.android.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.core.content.ContextCompat
import com.airwallex.android.core.AirwallexPlugins.AIRWALLEX_USER_AGENT
import com.airwallex.android.core.log.AirwallexLogger

class AirwallexWebView @SuppressLint("SetJavaScriptEnabled") constructor(
    context: Context,
    attrs: AttributeSet?
) : WebView(context, attrs) {
    constructor(context: Context) : this(context, null)

    var consoleErrorCallback: ((message: String, source: String?, line: Int) -> Unit)? = null

    init {
        setBackgroundColor(
            ContextCompat.getColor(
                context,
                R.color.airwallex_color_white
            )
        )
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.setSupportMultipleWindows(true)
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        settings.defaultTextEncodingName = "UTF-8"
        settings.userAgentString = AIRWALLEX_USER_AGENT

        webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                // Only process console messages if a callback is set
                val callback = consoleErrorCallback
                if (callback != null && consoleMessage != null) {
                    val level = consoleMessage.messageLevel()
                    if (level == ConsoleMessage.MessageLevel.ERROR) {
                        val message = consoleMessage.message()
                        val source = consoleMessage.sourceId()
                        val line = consoleMessage.lineNumber()

                        AirwallexLogger.error("WebView Console Error: $message at $source:$line")
                        callback.invoke(message, source, line)
                    }
                }
                return true
            }
        }
    }
}
