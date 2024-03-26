package com.airwallex.android.threedsecurity

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.airwallex.android.core.*
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.extension.putIfNotNull
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.log.ConsoleLogger
import com.airwallex.android.core.model.*
import com.airwallex.android.threedsecurity.exception.ThreeDSException
import com.airwallex.android.threedsecurity.exception.WebViewConnectionException
import com.airwallex.android.ui.AirwallexActivity
import com.airwallex.android.ui.AirwallexWebView
import com.airwallex.android.ui.destroyWebView
import java.lang.StringBuilder
import java.net.URLEncoder
import java.util.*

object ThreeDSecurityManager {
    fun handleThreeDSFlow(
        paymentIntentId: String,
        activity: Activity,
        nextAction: NextAction,
        cardNextActionModel: CardNextActionModel,
        listener: Airwallex.PaymentResultListener
    ) {
        val url = nextAction.url
        val data = nextAction.data

        if (url == null || data == null) {
            listener.onCompleted(AirwallexPaymentStatus.Failure(ThreeDSException("3DS Failed. Missing data in response.")))
            return
        }

        val container = activity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
        val webView = AirwallexWebView(activity).apply {
            if (nextAction.stage == NextAction.NextActionStage.WAITING_USER_INFO_INPUT) {
                visibility = View.VISIBLE
                // show loading only when the parent activity is provided by us.
                (activity as? AirwallexActivity)?.setLoadingProgress(loading = false)

                AnalyticsLogger.logPageView(
                    "webview_redirect",
                    mutableMapOf<String, Any>().apply {
                        putIfNotNull("stage", nextAction.stage?.value)
                    }
                )
            } else {
                visibility = View.INVISIBLE
            }
            webViewClient = ThreeDSecureWebViewClient(object : ThreeDSecureWebViewClient.Callbacks {
                override fun onWebViewConfirmation(payload: String) {
                    ConsoleLogger.debug("onWebViewConfirmation $payload")
                    visibility = View.INVISIBLE
                    (activity as? AirwallexActivity)?.setLoadingProgress(
                        loading = true,
                        cancelable = false
                    )
                    cardNextActionModel.paymentManager.startOperation(
                        build3DSContinuePaymentIntentOptions(
                            device = cardNextActionModel.device,
                            paymentIntentId = paymentIntentId,
                            clientSecret = cardNextActionModel.clientSecret,
                            threeDSecure = ThreeDSecure.Builder()
                                .setAcsResponse(payload)
                                .setReturnUrl(AirwallexPlugins.environment.threeDsReturnUrl())
                                .build()
                        ),
                        object : Airwallex.PaymentListener<PaymentIntent> {
                            override fun onSuccess(response: PaymentIntent) {
                                ConsoleLogger.debug("onSuccess $response")
                                destroyWebView()
                                val continueNextAction = response.nextAction
                                if (continueNextAction == null) {
                                    ConsoleLogger.debug("3DS finished, doesn't need challenge. Status: ${response.status}, NextAction: $continueNextAction")
                                    listener.onCompleted(AirwallexPaymentStatus.Success(response.id))
                                    return
                                }
                                handleThreeDSFlow(
                                    paymentIntentId,
                                    activity,
                                    continueNextAction,
                                    cardNextActionModel,
                                    listener
                                )
                            }

                            override fun onFailed(exception: AirwallexException) {
                                destroyWebView()
                                listener.onCompleted(AirwallexPaymentStatus.Failure(exception))
                            }
                        }
                    )
                }

                override fun onWebViewError(error: WebViewConnectionException) {
                    AnalyticsLogger.logError("webview_redirect", exception = error)
                    ConsoleLogger.error("onWebViewError", error)
                    destroyWebView()
                    listener.onCompleted(AirwallexPaymentStatus.Failure(error))
                }

                override fun onPageFinished(url: String?) {
                    ConsoleLogger.debug("onPageFinished $url")
                }

                override fun onPageStarted(url: String?) {
                    ConsoleLogger.debug("onPageStarted $url")
                }
            })

            val postResult = StringBuilder()
            for ((key, value) in data) {
                if (postResult.toString().isNotEmpty()) {
                    postResult.append("&")
                }
                postResult.append(key)
                postResult.append("=")
                postResult.append(URLEncoder.encode(value.toString(), "UTF-8"))
            }
            postUrl(url, postResult.toString().toByteArray())
        }
        container.addView(webView)
    }

    private fun build3DSContinuePaymentIntentOptions(
        device: Device?,
        paymentIntentId: String,
        clientSecret: String,
        threeDSecure: ThreeDSecure
    ): Options.ContinuePaymentIntentOptions {
        val request = PaymentIntentContinueRequest(
            requestId = UUID.randomUUID().toString(),
            type = PaymentIntentContinueType.THREE_DS_CONTINUE,
            threeDSecure = threeDSecure,
            device = device
        )
        return Options.ContinuePaymentIntentOptions(
            clientSecret = clientSecret,
            paymentIntentId = paymentIntentId,
            request = request
        )
    }
}