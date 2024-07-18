package com.airwallex.android.card

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment

import com.airwallex.android.card.exception.DccException
import com.airwallex.android.card.view.DccActivityLaunch
import com.airwallex.android.core.*
import com.airwallex.android.core.Airwallex.PaymentListener
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.model.*
import com.airwallex.android.threedsecurity.AirwallexSecurityConnector
import com.airwallex.android.threedsecurity.ThreeDSecurityActivityLaunch
import com.airwallex.android.threedsecurity.ThreeDSecurityManager
import com.airwallex.android.ui.AirwallexActivityLaunch

class CardComponent : ActionComponent {

    companion object {
        val PROVIDER: ActionComponentProvider<CardComponent> = CardComponentProvider()
    }

    private var dccCallback: DccCallback? = null
    private var listener: Airwallex.PaymentResultListener? = null

    override fun initialize(application: Application) {
        AirwallexActivityLaunch.initialize(application)
    }

    override fun handlePaymentIntentResponse(
        paymentIntentId: String,
        nextAction: NextAction?,
        fragment: Fragment?,
        activity: Activity,
        applicationContext: Context,
        cardNextActionModel: CardNextActionModel?,
        listener: Airwallex.PaymentResultListener,
        consentId: String?,
    ) {
        this.listener = listener
        if (cardNextActionModel == null) {
            listener.onCompleted(AirwallexPaymentStatus.Failure(AirwallexCheckoutException(message = "Card payment info not found")))
            return
        }

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
                ThreeDSecurityManager.handleThreeDSFlow(
                    paymentIntentId = paymentIntentId,
                    activity = activity,
                    fragment = fragment,
                    nextAction = nextAction,
                    cardNextActionModel = cardNextActionModel,
                    listener = listener
                ) { requestCode, resultCode, data ->
                    handleActivityResult(requestCode, resultCode, data)
                }
            }
            // payPayment
            else -> {
                startPaymentFlow(listener, cardNextActionModel, consentId)
            }
        }
    }

    private fun startPaymentFlow(
        listener: Airwallex.PaymentResultListener,
        cardNextActionModel: CardNextActionModel,
        consentId: String?,
    ) {
        val retrievePaymentIntentListener = object : PaymentListener<PaymentIntent> {
            override fun onSuccess(response: PaymentIntent) {
                listener.onCompleted(AirwallexPaymentStatus.Success(response.id, consentId))
            }

            override fun onFailed(exception: AirwallexException) {
                listener.onCompleted(AirwallexPaymentStatus.Failure(exception))
            }
        }
        cardNextActionModel.paymentManager.startOperation(
            Options.RetrievePaymentIntentOptions(
                clientSecret = cardNextActionModel.clientSecret,
                paymentIntentId = cardNextActionModel.paymentIntentId
            ),
            retrievePaymentIntentListener
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
        } else if (requestCode == ThreeDSecurityActivityLaunch.REQUEST_CODE) {
            listener?.let {
                val result = ThreeDSecurityActivityLaunch.Result.fromIntent(data)
                result?.paymentIntentId?.let { intentId ->
                    it.onCompleted(AirwallexPaymentStatus.Success(intentId))
                }
                result?.exception?.let { exception ->
                    it.onCompleted(AirwallexPaymentStatus.Failure(exception))
                }
                return true
            } ?: return false
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
