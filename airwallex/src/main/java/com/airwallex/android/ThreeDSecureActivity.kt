package com.airwallex.android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.android.exception.WebViewConnectionException
import com.airwallex.android.model.ThreeDSecureLookup
import com.cardinalcommerce.cardinalmobilesdk.Cardinal
import com.cardinalcommerce.cardinalmobilesdk.models.ValidateResponse
import kotlinx.android.synthetic.main.activity_threeds.*
import java.net.URLEncoder

internal class ThreeDSecureActivity : AppCompatActivity() {

    // The URL that we should intercept paRes and return
    private val termUrl: String
        get() {
            return "${AirwallexPlugins.baseUrl}/web/feedback"
        }

    private val threeDSecureLookup: ThreeDSecureLookup by lazy {
        requireNotNull(intent.getParcelableExtra<ThreeDSecureLookup>(EXTRA_THREE_D_SECURE_LOOKUP))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (threeDSecureLookup.version.startsWith("1.")) {
            // 3DS 1.0
            if (threeDSecureLookup.payload == null || threeDSecureLookup.acsUrl == null) {
                finishThreeDSecure1(null, false, "3DS failed. Missing PaReq or acs url.")
                return
            }

            setContentView(R.layout.activity_threeds)

            setSupportActionBar(toolbar)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.airwallex_ic_back)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            toolbar.setNavigationOnClickListener { onBackPressed() }

            pbLoading.max = 100
            pbLoading.progress = 1

            webView.webViewClient = ThreeDSecureWebViewClient(object : ThreeDSecureWebViewClient.Callbacks {
                override fun onWebViewConfirmation(payload: String) {
                    Logger.debug("3DS 1 onWebViewConfirmation $payload")
                    finishThreeDSecure1(payload, false, null)
                }

                override fun onWebViewError(error: WebViewConnectionException) {
                    Logger.debug("3DS 1 onWebViewError $error")
                    // Handle WebView connection failed
                    finishThreeDSecure1(null, false, error.message)
                }

                override fun onPageFinished(url: String?) {
                    pbLoading.visibility = View.GONE
                }

                override fun onPageStarted(url: String?) {
                    pbLoading.visibility = View.VISIBLE
                }
            })

            webView.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    pbLoading.progress = newProgress
                }
            }

            val payload = threeDSecureLookup.payload
            val acsUrl = threeDSecureLookup.acsUrl
            val postData = "&PaReq=" + URLEncoder.encode(payload, "UTF-8")
                .toString() + "&TermUrl=" + URLEncoder.encode(termUrl, "UTF-8")

            webView.postUrl(acsUrl, postData.toByteArray())
        } else {
            // 3DS 2.0
            Cardinal.getInstance().cca_continue(
                threeDSecureLookup.transactionId,
                threeDSecureLookup.payload,
                this
            ) { _, validateResponse, jwt -> finishThreeDSecure2(validateResponse, jwt) }
        }
    }

    override fun onBackPressed() {
        if (threeDSecureLookup.version.startsWith("1.")) {
            finishThreeDSecure1(null, true, null)
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (webView != null) {
            val root = window.decorView.findViewById<ViewGroup>(android.R.id.content)
            root.removeView(webView)
            webView.removeAllViews()
            webView.destroy()
        }
    }

    // 3DS 2.0
    private fun finishThreeDSecure2(validateResponse: ValidateResponse?, jwt: String?) {
        val result = Intent()
        result.putExtra(EXTRA_THREE_D_JWT, jwt)
        result.putExtra(EXTRA_VALIDATION_RESPONSE, validateResponse)

        val bundle = Bundle()
        bundle.putSerializable(
            EXTRA_THREE_D_SECURE_TYPE,
            ThreeDSecure.ThreeDSecureType.THREE_D_SECURE_2
        )
        result.putExtras(bundle)

        setResult(Activity.RESULT_OK, result)
        finish()
    }

    // 3DS 1.0
    private fun finishThreeDSecure1(payload: String? = null, cancel: Boolean, error: String?) {
        val result = Intent()
        result.putExtra(EXTRA_THREE_PAYLOAD, payload)
        result.putExtra(EXTRA_THREE_CANCEL, cancel)
        result.putExtra(EXTRA_THREE_FAILED_REASON, error)
        result.putExtra(EXTRA_THREE_TRANSACTION_ID, threeDSecureLookup.transactionId)

        val bundle = Bundle()
        bundle.putSerializable(
            EXTRA_THREE_D_SECURE_TYPE,
            ThreeDSecure.ThreeDSecureType.THREE_D_SECURE_1
        )
        result.putExtras(bundle)

        setResult(Activity.RESULT_OK, result)
        finish()
    }

    companion object {
        const val THREE_D_SECURE = 12345

        const val EXTRA_THREE_D_SECURE_TYPE = "EXTRA_THREE_D_SECURE_LOOKUP"

        // 2.0
        const val EXTRA_THREE_D_SECURE_LOOKUP = "EXTRA_THREE_D_SECURE_LOOKUP"
        const val EXTRA_THREE_D_JWT = "EXTRA_THREE_D_JWT"
        const val EXTRA_VALIDATION_RESPONSE = "EXTRA_VALIDATION_RESPONSE"

        // 1.0
        const val EXTRA_THREE_PAYLOAD = "EXTRA_THREE_PAYLOAD"
        const val EXTRA_THREE_TRANSACTION_ID = "EXTRA_THREE_TRANSACTION_ID"
        const val EXTRA_THREE_CANCEL = "EXTRA_THREE_CANCEL"
        const val EXTRA_THREE_FAILED_REASON = "EXTRA_THREE_FAILED_REASON"
    }
}
