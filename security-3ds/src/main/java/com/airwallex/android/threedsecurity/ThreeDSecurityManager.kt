package com.airwallex.android.threedsecurity

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.airwallex.android.core.*
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.extension.putIfNotNull
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.log.AirwallexLogger
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
    @Suppress("LongParameterList", "LongMethod")
    fun handleThreeDSFlow(
        paymentIntentId: String,
        fragment: Fragment?,
        nextAction: NextAction,
        cardNextActionModel: CardNextActionModel,
        listener: Airwallex.PaymentResultListener,
        paymentConsentId: String? = null,
        payload: String? = null
    ) {
        val url = nextAction.url
        val data = nextAction.data

        if (url == null || data == null) {
            listener.onCompleted(AirwallexPaymentStatus.Failure(ThreeDSException("3DS Failed. Missing data in response.")))
            return
        }

        val postResult = StringBuilder()
        for ((key, value) in data) {
            if (postResult.toString().isNotEmpty()) {
                postResult.append("&")
            }
            postResult.append(key)
            postResult.append("=")
            postResult.append(URLEncoder.encode(value.toString(), "UTF-8"))
        }

        if (nextAction.stage == NextAction.NextActionStage.WAITING_USER_INFO_INPUT) {
            AnalyticsLogger.logPageView(
                "webview_redirect",
                mutableMapOf<String, Any>().apply {
                    putIfNotNull("stage", nextAction.stage?.value)
                }
            )

            val threeDSecurityActivityLaunch = if (fragment != null) {
                ThreeDSecurityActivityLaunch(fragment)
            } else {
                ThreeDSecurityActivityLaunch(cardNextActionModel.activityProvider())
            }
            threeDSecurityActivityLaunch.launchForResult(
                ThreeDSecurityActivityLaunch.Args(
                    url = url,
                    body = postResult.toString(),
                    options = build3DSContinuePaymentIntentOptions(
                        device = cardNextActionModel.device,
                        paymentIntentId = paymentIntentId,
                        clientSecret = cardNextActionModel.clientSecret,
                        threeDSecure = ThreeDSecure.Builder()
                            .setAcsResponse(payload)
                            .setReturnUrl(AirwallexPlugins.environment.threeDsReturnUrl())
                            .build()
                    )
                )
            ) { _, result ->
                handleThreeDSActivityResult(
                    paymentConsentId = paymentConsentId,
                    data = result.data,
                    listener = listener
                )
            }
        } else {
            (cardNextActionModel.activityProvider() as? AirwallexActivity)?.setLoadingProgress(
                loading = true,
                cancelable = false
            )
            val container = cardNextActionModel.activityProvider().window.decorView.findViewById<ViewGroup>(android.R.id.content)

            val webView = AirwallexWebView(cardNextActionModel.activityProvider()).apply {
                visibility = View.INVISIBLE

                this@apply.webViewClient =
                    ThreeDSecureWebViewClient(
                        { cardNextActionModel.activityProvider().lifecycleScope },
                        object : ThreeDSecureWebViewClient.Callbacks {
                            @Suppress("LongMethod")
                            override fun onWebViewConfirmation(payload: String) {
                                val options = build3DSContinuePaymentIntentOptions(
                                    device = cardNextActionModel.device,
                                    paymentIntentId = paymentIntentId,
                                    clientSecret = cardNextActionModel.clientSecret,
                                    threeDSecure = ThreeDSecure.Builder()
                                        .setAcsResponse(payload)
                                        .setReturnUrl(AirwallexPlugins.environment.threeDsReturnUrl())
                                        .build()
                                )

                                AirwallexLogger.info(
                                    "ThreeDSecurityManager onWebViewConfirmation: nextAction.stage = ${nextAction.stage}",
                                    sensitiveMessage = "payload = $payload"
                                )
                                if (nextAction.stage == NextAction.NextActionStage.WAITING_USER_INFO_INPUT) {
                                    (cardNextActionModel.activityProvider() as? AirwallexActivity)?.setLoadingProgress(
                                        false
                                    )
                                    AnalyticsLogger.logPageView(
                                        "webview_redirect",
                                        mutableMapOf<String, Any>().apply {
                                            putIfNotNull("stage", nextAction.stage?.value)
                                        }
                                    )
                                    ThreeDSecurityActivityLaunch(cardNextActionModel.activityProvider())
                                        .launchForResult(
                                            ThreeDSecurityActivityLaunch.Args(
                                                url = url,
                                                body = postResult.toString(),
                                                options = options
                                            )
                                        ) { _, _ ->
                                            // The result will be handled in onWebViewConfirmation callback
                                        }
                                } else {
                                    cardNextActionModel.paymentManager.startOperation(
                                        options,
                                        object : Airwallex.PaymentListener<PaymentIntent> {
                                            override fun onSuccess(response: PaymentIntent) {
                                                AirwallexLogger.info(
                                                    "ThreeDSecurityManager: onSuccess ",
                                                    sensitiveMessage = response.toString()
                                                )
                                                destroyWebView()
                                                val continueNextAction = response.nextAction
                                                if (continueNextAction == null) {
                                                    AirwallexLogger.info("ThreeDSecurityManager: 3DS finished, doesn't need challenge. Status: ${response.status}, NextAction: $continueNextAction")
                                                    listener.onCompleted(
                                                        AirwallexPaymentStatus.Success(
                                                            paymentIntentId = response.id,
                                                            consentId = paymentConsentId
                                                        )
                                                    )
                                                    return
                                                }
                                                handleThreeDSFlow(
                                                    paymentIntentId,
                                                    fragment,
                                                    continueNextAction,
                                                    cardNextActionModel,
                                                    listener,
                                                    paymentConsentId,
                                                    payload
                                                )
                                            }

                                            override fun onFailed(exception: AirwallexException) {
                                                destroyWebView()
                                                AirwallexLogger.error(
                                                    "ThreeDSecurityManager: onFailed",
                                                    exception
                                                )
                                                listener.onCompleted(
                                                    AirwallexPaymentStatus.Failure(
                                                        exception
                                                    )
                                                )
                                            }
                                        }
                                    )
                                }
                            }

                            override fun onWebViewError(error: WebViewConnectionException) {
                                AnalyticsLogger.logError("webview_redirect", exception = error)
                                AirwallexLogger.error("onWebViewError", error)
                                destroyWebView()
                                listener.onCompleted(AirwallexPaymentStatus.Failure(error))
                            }

                            override fun onPageFinished(url: String?) {
                                AirwallexLogger.debug("onPageFinished $url")
                            }

                            override fun onPageStarted(url: String?) {
                                AirwallexLogger.debug("onPageStarted $url")
                            }
                        }
                    )

                postUrl(url, postResult.toString().toByteArray())
            }
            container.addView(webView)
        }
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

    private fun handleThreeDSActivityResult(
        paymentConsentId: String?,
        data: Intent?,
        listener: Airwallex.PaymentResultListener
    ) {
        val result = ThreeDSecurityActivityLaunch.Result.fromIntent(data)
        result?.paymentIntentId?.let { intentId ->
            listener.onCompleted(
                AirwallexPaymentStatus.Success(
                    paymentIntentId = intentId,
                    consentId = paymentConsentId
                )
            )
        }
        result?.exception?.let { exception ->
            listener.onCompleted(AirwallexPaymentStatus.Failure(exception))
        }
    }
}