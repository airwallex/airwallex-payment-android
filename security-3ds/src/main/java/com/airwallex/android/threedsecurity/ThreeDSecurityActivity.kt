package com.airwallex.android.threedsecurity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexApiRepository
import com.airwallex.android.core.AirwallexPaymentManager
import com.airwallex.android.core.PaymentManager
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.log.ConsoleLogger
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.threedsecurity.databinding.ActivityThreeDSecurityBinding
import com.airwallex.android.threedsecurity.exception.WebViewConnectionException
import com.airwallex.android.ui.AirwallexActivity

class ThreeDSecurityActivity : AirwallexActivity() {

    private val viewBinding: ActivityThreeDSecurityBinding by lazy {
        viewStub.layoutResource = R.layout.activity_three_d_security
        val root = viewStub.inflate() as ViewGroup
        ActivityThreeDSecurityBinding.bind(root)
    }

    private val args: ThreeDSecurityActivityLaunch.Args by lazy {
        ThreeDSecurityActivityLaunch.Args.getExtra(intent)
    }

    private val paymentManager: PaymentManager by lazy {
        AirwallexPaymentManager(AirwallexApiRepository())
    }

    override fun onBackButtonPressed() {
        finishWithData(exception = AirwallexCheckoutException(message = "3DS has been cancelled!"))
    }

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_close
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val webView = viewBinding.webView.apply {
            webViewClient = ThreeDSecureWebViewClient(object : ThreeDSecureWebViewClient.Callbacks {
                override fun onWebViewConfirmation(payload: String) {
                    ConsoleLogger.debug("onWebViewConfirmation $payload")
                    paymentManager.startOperation(
                        args.options,
                        object : Airwallex.PaymentListener<PaymentIntent> {
                            override fun onSuccess(response: PaymentIntent) {
                                ConsoleLogger.debug("onSuccess $response")
                                val continueNextAction = response.nextAction
                                if (continueNextAction == null) {
                                    ConsoleLogger.debug("3DS finished, doesn't need challenge. Status: ${response.status}, NextAction: $continueNextAction")
                                    finishWithData(paymentIntentId = response.id)
                                }
                            }

                            override fun onFailed(exception: AirwallexException) {
                                finishWithData(exception = exception)
                            }
                        }
                    )
                }

                override fun onWebViewError(error: WebViewConnectionException) {
                    AnalyticsLogger.logError("webview_redirect", exception = error)
                    ConsoleLogger.error("onWebViewError", error)
                    finishWithData(exception = error)
                }

                override fun onPageFinished(url: String?) {
                    ConsoleLogger.debug("onPageFinished $url")
                }

                override fun onPageStarted(url: String?) {
                    ConsoleLogger.debug("onPageStarted $url")
                }
            })
        }
        webView.postUrl(args.url, args.body.toByteArray())
    }

    override fun onBackPressed() {
        finishWithData(exception = AirwallexCheckoutException(message = "3DS has been cancelled!"))
        super.onBackPressed()
    }

    private fun finishWithData(
        paymentIntentId: String? = null,
        exception: AirwallexException? = null
    ) {
        setResult(
            RESULT_OK,
            Intent().putExtras(
                ThreeDSecurityActivityLaunch.Result(
                    paymentIntentId = paymentIntentId,
                    exception = exception
                ).toBundle()
            )
        )
        finish()
    }

}