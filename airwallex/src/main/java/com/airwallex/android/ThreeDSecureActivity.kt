package com.airwallex.android

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.android.ThreeDSecure.THREE_DS_RETURN_URL
import com.airwallex.android.model.ThreeDSecureLookup
import com.cardinalcommerce.cardinalmobilesdk.Cardinal
import com.cardinalcommerce.cardinalmobilesdk.models.ValidateResponse
import com.cardinalcommerce.cardinalmobilesdk.services.CardinalValidateReceiver
import kotlinx.android.synthetic.main.activity_threeds.*
import java.net.URLEncoder

class ThreeDSecureActivity : AppCompatActivity(), CardinalValidateReceiver {

    companion object {
        const val EXTRA_THREE_D_SECURE_LOOKUP = "EXTRA_THREE_D_SECURE_LOOKUP"
        const val EXTRA_THREE_D_JWT = "EXTRA_THREE_D_JWT"
        const val EXTRA_VALIDATION_RESPONSE = "EXTRA_VALIDATION_RESPONSE"
        const val THREE_D_SECURE = 12345
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_threeds)

        var extras = intent.extras

        if (extras == null) {
            extras = Bundle()
        }

        val threeDSecureLookup: ThreeDSecureLookup =
            extras.getParcelable(EXTRA_THREE_D_SECURE_LOOKUP)!!

        if (threeDSecureLookup.dsData.version?.startsWith("1.") == true) {
            initWebView()
            loadUrl(webView, threeDSecureLookup)
        } else {
            webView.visibility = View.GONE
            Cardinal.getInstance().cca_continue(
                threeDSecureLookup.transactionId,
                threeDSecureLookup.payload,
                this,
                this
            )
        }
    }

    private fun loadUrl(webView: WebView, threeDSecureLookup: ThreeDSecureLookup) {
        val payload = threeDSecureLookup.payload
        val termUrl = "$THREE_DS_RETURN_URL/$payload"
        val acsUrl = threeDSecureLookup.acsUrl
        val postData = "&PaReq=" + URLEncoder.encode(payload, "UTF-8")
            .toString() + "&TermUrl=" + URLEncoder.encode(termUrl, "UTF-8")

        Log.e("aaa", "termUrl $termUrl")
        webView.postUrl(acsUrl, postData.toByteArray())
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webView.visibility = View.VISIBLE
        webView.webViewClient = ThreeDSWebViewClient()
        webView.webChromeClient = WebChromeClient()

        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true

        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE

        webSettings.setSupportMultipleWindows(true)
        webSettings.defaultTextEncodingName = "utf-8"
    }

    class ThreeDSWebViewClient : WebViewClient() {

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest
        ): WebResourceResponse? {
            Log.e("aaa", "request.url 11111" + request.url)
            return super.shouldInterceptRequest(view, request)
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            Log.e("aaa", "url" + url)
            return super.shouldOverrideUrlLoading(view, url)
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {

            Log.e("aaa", "request.url 22222" + request?.url)
            return super.shouldOverrideUrlLoading(view, request)
        }
    }

    override fun onValidated(p0: Context?, validateResponse: ValidateResponse?, jwt: String?) {
        val result = Intent()
        result.putExtra(EXTRA_THREE_D_JWT, jwt)
        result.putExtra(EXTRA_VALIDATION_RESPONSE, validateResponse)

        setResult(Activity.RESULT_OK, result)
        finish()
    }
}
