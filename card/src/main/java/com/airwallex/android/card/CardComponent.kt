package com.airwallex.android.card

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup

import com.airwallex.android.card.exception.DccException
import com.airwallex.android.card.exception.ThreeDSException
import com.airwallex.android.card.exception.WebViewConnectionException
import com.airwallex.android.card.view.DccActivityLaunch
import com.airwallex.android.core.*
import com.airwallex.android.core.Airwallex.PaymentListener
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.log.Logger
import com.airwallex.android.core.model.*
import com.airwallex.android.ui.AirwallexActivity
import com.airwallex.android.ui.AirwallexWebView
import com.airwallex.android.ui.destroyWebView
import java.lang.StringBuilder
import java.net.URLEncoder
import java.util.*

class CardComponent : ActionComponent {

    companion object {
        val PROVIDER: ActionComponentProvider<CardComponent> = CardComponentProvider()
    }

    private var dccCallback: DccCallback? = null

    override fun handlePaymentIntentResponse(
        paymentIntentId: String,
        nextAction: NextAction?,
        activity: Activity,
        applicationContext: Context,
        cardNextActionModel: CardNextActionModel?,
        listener: Airwallex.PaymentResultListener
    ) {
        if (cardNextActionModel == null) {
            listener.onCompleted(AirwallexPaymentStatus.Failure(AirwallexCheckoutException(message = "Card payment info not found")))
            return
        }

        val fragment = cardNextActionModel.fragment
        val dccActivityLaunch: DccActivityLaunch = if (fragment != null) {
            DccActivityLaunch(fragment)
        } else {
            DccActivityLaunch(activity)
        }

        when (nextAction?.type) {
            // DCC flow
            NextAction.NextActionType.DCC -> {
                val dcc = nextAction.dcc
                if (dcc == null) {
                    listener.onCompleted(
                        AirwallexPaymentStatus.Failure(
                            AirwallexCheckoutException(
                                message = "Dcc data not found"
                            )
                        )
                    )
                    return
                }
                dccCallback = object : DccCallback {
                    override fun onSuccess(paymentIntentId: String) {
                        dccCallback = null
                        listener.onCompleted(AirwallexPaymentStatus.Success(paymentIntentId))
                    }

                    override fun onFailed(exception: Exception) {
                        dccCallback = null
                        listener.onCompleted(
                            AirwallexPaymentStatus.Failure(
                                AirwallexCheckoutException(e = exception)
                            )
                        )
                    }
                }

                // DCC flow, please select your currency
                dccActivityLaunch.startForResult(
                    DccActivityLaunch.Args(
                        dcc = dcc,
                        paymentIntentId = cardNextActionModel.paymentIntentId,
                        currency = cardNextActionModel.currency,
                        amount = cardNextActionModel.amount,
                        clientSecret = cardNextActionModel.clientSecret
                    )
                )
            }
            // 3DS flow
            NextAction.NextActionType.REDIRECT_FORM -> {
                handleThreeDSFlow(
                    paymentIntentId = paymentIntentId,
                    activity = activity,
                    nextAction = nextAction,
                    cardNextActionModel = cardNextActionModel,
                    listener = listener
                )
            }
            else -> {
                val retrievePaymentIntentListener = object : PaymentListener<PaymentIntent> {
                    override fun onSuccess(response: PaymentIntent) {
                        listener.onCompleted(AirwallexPaymentStatus.Success(response.id))
                    }

                    override fun onFailed(exception: AirwallexException) {
                        listener.onCompleted(AirwallexPaymentStatus.Failure(exception))
                    }
                }
                cardNextActionModel.paymentManager.startOperation(
                    AirwallexApiRepository.RetrievePaymentIntentOptions(
                        clientSecret = cardNextActionModel.clientSecret,
                        paymentIntentId = cardNextActionModel.paymentIntentId
                    ),
                    retrievePaymentIntentListener
                )
            }
        }
    }

    private fun handleThreeDSFlow(
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
                (activity as AirwallexActivity).setLoadingProgress(loading = false)
            } else {
                visibility = View.INVISIBLE
            }
            webViewClient = ThreeDSecureWebViewClient(object : ThreeDSecureWebViewClient.Callbacks {
                override fun onWebViewConfirmation(payload: String) {
                    Logger.debug("onWebViewConfirmation $payload")
                    visibility = View.INVISIBLE
                    (activity as AirwallexActivity).setLoadingProgress(
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
                        object : PaymentListener<PaymentIntent> {
                            override fun onSuccess(response: PaymentIntent) {
                                Logger.debug("onSuccess $response")
                                destroyWebView()
                                val continueNextAction = response.nextAction
                                if (continueNextAction == null) {
                                    Logger.debug("3DS finished, doesn't need challenge. Status: ${response.status}, NextAction: $continueNextAction")
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
                    Logger.error("onWebViewError", error)
                    destroyWebView()
                    listener.onCompleted(AirwallexPaymentStatus.Failure(error))
                }

                override fun onPageFinished(url: String?) {
                    Logger.debug("onPageFinished $url")
                    if (url?.contains("challengeRequest") == true) {
                        visibility = View.VISIBLE
                        (activity as AirwallexActivity).setLoadingProgress(loading = false)
                    }
                }

                override fun onPageStarted(url: String?) {
                    Logger.debug("onPageStarted $url")
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
    ): AirwallexApiRepository.ContinuePaymentIntentOptions {
        val request = PaymentIntentContinueRequest(
            requestId = UUID.randomUUID().toString(),
            type = PaymentIntentContinueType.THREE_DS_CONTINUE,
            threeDSecure = threeDSecure,
            device = device
        )
        return AirwallexApiRepository.ContinuePaymentIntentOptions(
            clientSecret = clientSecret,
            paymentIntentId = paymentIntentId,
            request = request
        )
    }

    override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode == DccActivityLaunch.REQUEST_CODE) {
            dccCallback?.let {
                try {
                    handleDccData(data, resultCode, it)
                } catch (e: Exception) {
                    it.onFailed(DccException(message = e.localizedMessage ?: "Dcc failed."))
                }
            }
            return true
        }
        return false
    }

    override fun retrieveSecurityToken(
        paymentIntentId: String,
        applicationContext: Context,
        securityTokenListener: SecurityTokenListener
    ) {
        AirwallexSecurityConnector().retrieveSecurityToken(
            paymentIntentId,
            applicationContext,
            securityTokenListener
        )
    }

    private fun handleDccData(
        data: Intent?,
        resultCode: Int,
        callback: DccCallback
    ) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val result = DccActivityLaunch.Result.fromIntent(data)
                val paymentIntentId = result?.paymentIntentId
                if (paymentIntentId != null) {
                    callback.onSuccess(paymentIntentId)
                } else {
                    callback.onFailed(result?.exception ?: DccException(message = "Dcc failed."))
                }
            }
            Activity.RESULT_CANCELED -> {
                callback.onFailed(DccException(message = "Dcc failed. Reason: User cancel the Dcc"))
            }
        }
    }
}
