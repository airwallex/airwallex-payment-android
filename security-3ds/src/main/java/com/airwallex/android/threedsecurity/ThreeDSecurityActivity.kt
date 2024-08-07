package com.airwallex.android.threedsecurity

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexApiRepository
import com.airwallex.android.core.AirwallexPaymentManager
import com.airwallex.android.core.PaymentManager
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.threedsecurity.databinding.ActivityThreeDSecurityBinding
import com.airwallex.android.threedsecurity.exception.WebViewConnectionException
import com.airwallex.android.ui.AirwallexActivity
import com.airwallex.android.ui.extension.getExtraArgs

class ThreeDSecurityActivity : AirwallexActivity() {

    private val viewBinding: ActivityThreeDSecurityBinding by lazy {
        viewStub.layoutResource = R.layout.activity_three_d_security
        val root = viewStub.inflate() as ViewGroup
        ActivityThreeDSecurityBinding.bind(root)
    }

    private val args: ThreeDSecurityActivityLaunch.Args by lazy {
        intent.getExtraArgs()
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
                    AirwallexLogger.info("ThreeDSecurityActivity onWebViewConfirmation", sensitiveMessage = "payload = $payload")
                    paymentManager.startOperation(
                        args.options,
                        object : Airwallex.PaymentListener<PaymentIntent> {
                            override fun onSuccess(response: PaymentIntent) {
                                AirwallexLogger.info(
                                    "ThreeDSecurityActivity onSuccess ",
                                    sensitiveMessage = response.toString()
                                )
                                val continueNextAction = response.nextAction
                                if (continueNextAction == null) {
                                    AirwallexLogger.info("ThreeDSecurityActivity 3DS finished, doesn't need challenge. Status: ${response.status}, NextAction: $continueNextAction")
                                    finishWithData(paymentIntentId = response.id)
                                }
                            }

                            override fun onFailed(exception: AirwallexException) {
                                AirwallexLogger.error("ThreeDSecurityActivity onFailed", exception)
                                finishWithData(exception = exception)
                            }
                        }
                    )
                }

                override fun onWebViewError(error: WebViewConnectionException) {
                    AnalyticsLogger.logError("webview_redirect", exception = error)
                    AirwallexLogger.error("onWebViewError", error)
                    finishWithData(exception = error)
                }

                override fun onPageFinished(url: String?) {
                    AirwallexLogger.debug("onPageFinished $url")
                }

                override fun onPageStarted(url: String?) {
                    AirwallexLogger.debug("onPageStarted $url")
                }
            })
        }
        webView.postUrl(args.url, args.body.toByteArray())
    }

    private fun finishWithData(
        paymentIntentId: String? = null,
        exception: AirwallexException? = null
    ) {
        AirwallexLogger.info("ThreeDSecurityActivity finishWithData")
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