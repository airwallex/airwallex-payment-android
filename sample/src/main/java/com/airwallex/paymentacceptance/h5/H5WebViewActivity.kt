package com.airwallex.paymentacceptance.h5

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.airwallex.android.core.AirwallexPlugins.AIRWALLEX_USER_AGENT
import com.airwallex.paymentacceptance.databinding.ActivityH5WebviewBinding

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
            // Append custom identifier to default user agent (preserves Chrome/WebView detection for Google Pay)
            settings.userAgentString = "${settings.userAgentString} $AIRWALLEX_USER_AGENT"
            if (WebViewFeature.isFeatureSupported(
                    WebViewFeature.PAYMENT_REQUEST)) {
                WebSettingsCompat.setPaymentRequestEnabled(settings, true);
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
                        url.startsWith("airwallexcheckout")
                    ) {
                        try {
                            val intent = Intent()
                            intent.action = Intent.ACTION_VIEW
                            intent.data = Uri.parse(url)
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
