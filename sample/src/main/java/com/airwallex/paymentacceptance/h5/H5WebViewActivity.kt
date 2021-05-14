package com.airwallex.paymentacceptance.h5

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.paymentacceptance.databinding.ActivityH5WebviewBinding

class H5WebViewActivity : AppCompatActivity() {

    companion object {
        const val URL = "URL"
        const val REFERER = "REFERER"
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

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
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
                        } catch (e: ActivityNotFoundException) {
                        }
                        return true
                    } else {
                        val extraHeaders: MutableMap<String, String> =
                            HashMap()
                        extraHeaders["Referer"] = "https://checkout.airwallex.com"
                        view!!.loadUrl(url, extraHeaders)
                    }
                    return true
                }
            }
        }

        val url = requireNotNull(intent.getStringExtra(URL))
        val referer = requireNotNull(intent.getStringExtra(REFERER))
        val refererUrl =
            if (referer.startsWith("http") || referer.startsWith("https") || referer.startsWith("android-app")) {
                referer
            } else {
                "https://$referer"
            }
        viewBinding.webview.loadUrl(url, mapOf<String, String>("Referer" to refererUrl))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
