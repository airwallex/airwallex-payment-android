package com.airwallex.paymentacceptance.h5

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.android.core.AirwallexPlugins.AIRWALLEX_USER_AGENT
import com.airwallex.paymentacceptance.databinding.ActivityH5WebviewBinding
import androidx.core.net.toUri

class H5WebViewActivity : AppCompatActivity() {

    companion object {
        const val URL = "URL"
        const val REFERER = "REFERER"
    }

    private val url by lazy {
        requireNotNull(intent.getStringExtra(URL))
    }
    private val referer by lazy {
        requireNotNull(intent.getStringExtra(REFERER))
    }

    private val viewBinding: ActivityH5WebviewBinding by lazy {
        ActivityH5WebviewBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""

        viewBinding.webview.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.setSupportMultipleWindows(true)
            settings.cacheMode = WebSettings.LOAD_NO_CACHE
            settings.defaultTextEncodingName = "UTF-8"
            settings.userAgentString = AIRWALLEX_USER_AGENT

            webChromeClient = object : WebChromeClient() {
                override fun onCreateWindow(
                    view: WebView?,
                    isDialog: Boolean,
                    isUserGesture: Boolean,
                    resultMsg: android.os.Message?
                ): Boolean {
                    // Create a dialog to host the popup WebView
                    val dialog = Dialog(this@H5WebViewActivity, android.R.style.Theme_NoTitleBar_Fullscreen)
                    dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

                    val newWebView = WebView(this@H5WebViewActivity)
                    newWebView.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    // Configure the popup WebView with same settings
                    newWebView.settings.javaScriptEnabled = true
                    newWebView.settings.domStorageEnabled = true
                    newWebView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
                    newWebView.settings.userAgentString = AIRWALLEX_USER_AGENT
                    newWebView.settings.setSupportMultipleWindows(true)

                    // Set up WebViewClient for the popup
                    newWebView.webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val url = request?.url.toString()
                            if (url.startsWith("weixin://wap/pay?") ||
                                url.startsWith("http://weixin/wap/pay") ||
                                url.startsWith("alipays") ||
                                url.startsWith("alipayhk") ||
                                url.startsWith("alipayconnect") ||
                                url.startsWith("airwallexcheckout")
                            ) {
                                try {
                                    val intent = Intent()
                                    intent.action = Intent.ACTION_VIEW
                                    intent.data = url.toUri()
                                    startActivity(intent)
                                    dialog.dismiss()
                                } catch (e: ActivityNotFoundException) {
                                    e.printStackTrace()
                                }
                                return true
                            }
                            return false
                        }
                    }

                    // Set up WebChromeClient for the popup to handle window.close()
                    newWebView.webChromeClient = object : WebChromeClient() {
                        override fun onCloseWindow(window: WebView?) {
                            dialog.dismiss()
                        }
                    }

                    // Add the WebView to the dialog
                    dialog.setContentView(newWebView)
                    dialog.show()

                    // Clean up when dialog is dismissed
                    dialog.setOnDismissListener {
                        newWebView.destroy()
                    }

                    val transport = resultMsg?.obj as? WebView.WebViewTransport
                    transport?.webView = newWebView
                    resultMsg?.sendToTarget()
                    return true
                }
            }

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val url = request?.url.toString()
                    if (url.startsWith("weixin://wap/pay?") ||
                        url.startsWith("http://weixin/wap/pay") ||
                        url.startsWith("alipays") ||
                        url.startsWith("alipayhk") ||
                        url.startsWith("alipayconnect") ||
                        url.startsWith("airwallexcheckout")
                    ) {
                        try {
                            val intent = Intent()
                            intent.action = Intent.ACTION_VIEW
                            intent.data = url.toUri()
                            startActivity(intent)
                            finish()
                        } catch (e: ActivityNotFoundException) {
                            e.printStackTrace()
                        }
                        return true
                    } else {
                        view?.loadUrl(url, mapOf<String, String>("Referer" to referer))
                    }
                    return true
                }
            }
        }
        viewBinding.webview.loadUrl(url, mapOf<String, String>("Referer" to referer))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
