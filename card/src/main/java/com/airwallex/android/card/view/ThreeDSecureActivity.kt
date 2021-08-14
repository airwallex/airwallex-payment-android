package com.airwallex.android.card.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.airwallex.android.card.R
import com.airwallex.android.card.ThreeDSecureManager
import com.airwallex.android.card.ThreeDSecureWebViewClient
import com.airwallex.android.card.databinding.ActivityThreedsBinding
import com.airwallex.android.card.exception.WebViewConnectionException
import com.airwallex.android.core.AirwallexApiRepository
import com.airwallex.android.core.log.Logger
import com.airwallex.android.ui.AirwallexActivity
import com.cardinalcommerce.cardinalmobilesdk.Cardinal
import com.cardinalcommerce.cardinalmobilesdk.models.ValidateResponse
import java.net.URLEncoder

class ThreeDSecureActivity : AirwallexActivity() {

    private val viewBinding: ActivityThreedsBinding by lazy {
        viewStub.layoutResource = R.layout.activity_threeds
        val root = viewStub.inflate() as ViewGroup
        ActivityThreedsBinding.bind(root)
    }

    private val args: ThreeDSecureActivityLaunch.Args by lazy { ThreeDSecureActivityLaunch.Args.getExtra(intent) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val threeDSecureLookup = args.threeDSecureLookup

        if (threeDSecureLookup.version.startsWith("1.")) {
            // 3DS 1.0
            val payload = threeDSecureLookup.payload
            val acsUrl = threeDSecureLookup.acsUrl

            if (payload == null || acsUrl == null) {
                finishThreeDSecure1(null, false, "3DS failed. Missing PaReq or acs url.")
                return
            }

            toolbar.setNavigationOnClickListener { onBackPressed() }

            viewBinding.pbLoading.max = 100
            viewBinding.pbLoading.progress = 1

            viewBinding.webView.webViewClient = ThreeDSecureWebViewClient(object : ThreeDSecureWebViewClient.Callbacks {
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
                    viewBinding.pbLoading.visibility = View.GONE
                }

                override fun onPageStarted(url: String?) {
                    viewBinding.pbLoading.visibility = View.VISIBLE
                }
            })

            viewBinding.webView.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    viewBinding.pbLoading.progress = newProgress
                }
            }

            val postData = String.format(
                "PaReq=%s&TermUrl=%s",
                URLEncoder.encode(payload, "UTF-8"),
                URLEncoder.encode(AirwallexApiRepository.paResTermUrl(), "UTF-8")
            )
            viewBinding.webView.postUrl(acsUrl, postData.toByteArray())
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
        if (args.threeDSecureLookup.version.startsWith("1.")) {
            finishThreeDSecure1(null, true, null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cleanup Cardinal
        Cardinal.getInstance().cleanup()
        val root = window.decorView.findViewById<ViewGroup>(android.R.id.content)
        root.removeView(viewBinding.webView)
        viewBinding.webView.removeAllViews()
        viewBinding.webView.destroy()
    }

    override fun onActionSave() {
        // Ignore
    }

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_back
    }

    // 3DS 2.0
    private fun finishThreeDSecure2(validateResponse: ValidateResponse?, jwt: String?) {
        val result = Intent()
        result.putExtra(EXTRA_THREE_D_JWT, jwt)
        result.putExtra(EXTRA_VALIDATION_RESPONSE, validateResponse)

        val bundle = Bundle()
        bundle.putSerializable(
            EXTRA_THREE_D_SECURE_TYPE,
            ThreeDSecureManager.ThreeDSecureType.THREE_D_SECURE_2
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

        val bundle = Bundle()
        bundle.putSerializable(
            EXTRA_THREE_D_SECURE_TYPE,
            ThreeDSecureManager.ThreeDSecureType.THREE_D_SECURE_1
        )
        result.putExtras(bundle)

        setResult(Activity.RESULT_OK, result)
        finish()
    }

    companion object {
        const val EXTRA_THREE_D_SECURE_TYPE = "EXTRA_THREE_D_SECURE_LOOKUP"

        // 2.0
        const val EXTRA_THREE_D_JWT = "EXTRA_THREE_D_JWT"
        const val EXTRA_VALIDATION_RESPONSE = "EXTRA_VALIDATION_RESPONSE"

        // 1.0
        const val EXTRA_THREE_PAYLOAD = "EXTRA_THREE_PAYLOAD"
        const val EXTRA_THREE_CANCEL = "EXTRA_THREE_CANCEL"
        const val EXTRA_THREE_FAILED_REASON = "EXTRA_THREE_FAILED_REASON"
    }
}
