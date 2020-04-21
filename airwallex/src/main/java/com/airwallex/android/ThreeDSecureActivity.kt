package com.airwallex.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.android.ThreeDSecure.THREE_DS_RETURN_URL
import com.airwallex.android.exception.WebViewConnectionException
import com.airwallex.android.model.ThreeDSecureLookup
import com.cardinalcommerce.cardinalmobilesdk.Cardinal
import com.cardinalcommerce.cardinalmobilesdk.models.ValidateResponse
import com.cardinalcommerce.cardinalmobilesdk.services.CardinalValidateReceiver
import kotlinx.android.synthetic.main.activity_threeds.*
import java.net.URLEncoder

class ThreeDSecureActivity : AppCompatActivity(), CardinalValidateReceiver,
    ThreeDSecureWebViewClient.Callbacks {

    companion object {
        const val EXTRA_THREE_D_SECURE_LOOKUP = "EXTRA_THREE_D_SECURE_LOOKUP"
        const val EXTRA_THREE_D_JWT = "EXTRA_THREE_D_JWT"
        const val EXTRA_VALIDATION_RESPONSE = "EXTRA_VALIDATION_RESPONSE"
        const val THREE_D_SECURE = 12345
        const val TAG = "ThreeDSecureActivity"
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

    private fun initWebView() {
        webView.visibility = View.VISIBLE
        webView.webViewClient = ThreeDSecureWebViewClient(this)
        webView.webChromeClient = WebChromeClient()
    }

    private fun loadUrl(webView: WebView, threeDSecureLookup: ThreeDSecureLookup) {
        val payload = threeDSecureLookup.payload
        val termUrl = "$THREE_DS_RETURN_URL/$payload"
        val acsUrl = threeDSecureLookup.acsUrl
        val postData = "&PaReq=" + URLEncoder.encode(payload, "UTF-8")
            .toString() + "&TermUrl=" + URLEncoder.encode(termUrl, "UTF-8")

        webView.postUrl(acsUrl, postData.toByteArray())
    }

    override fun onValidated(p0: Context?, validateResponse: ValidateResponse?, jwt: String?) {
        val result = Intent()
        result.putExtra(EXTRA_THREE_D_JWT, jwt)
        result.putExtra(EXTRA_VALIDATION_RESPONSE, validateResponse)

        setResult(Activity.RESULT_OK, result)
        finish()
    }

    override fun onWebViewConfirmation(payload: String) {
        if (payload.isNotEmpty()) {
            Logger.debug(TAG, "payload $payload")
            // add logic here for valid payload (cmpi_authenticate) and close webview using finish() or open another Activity
        } else {
            // handle invalid/empty payload
        }
    }

    override fun onWebViewError(error: WebViewConnectionException) {
        // Handle WebView connection failed
    }
}
