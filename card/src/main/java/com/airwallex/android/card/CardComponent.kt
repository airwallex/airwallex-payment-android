package com.airwallex.android.card

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment

import com.airwallex.android.core.*
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.model.*
import com.airwallex.android.threedsecurity.AirwallexSecurityConnector
import com.airwallex.android.threedsecurity.ThreeDSecurityActivityLaunch
import com.airwallex.android.threedsecurity.ThreeDSecurityManager
import com.airwallex.android.ui.AirwallexActivityLaunch

class CardComponent : ActionComponent {

    companion object {
        val PROVIDER: ActionComponentProvider<CardComponent> = CardComponentProvider()
    }

    private var listener: Airwallex.PaymentResultListener? = null

    override fun initialize(application: Application) {
        AirwallexActivityLaunch.initialize(application)
        AirwallexSecurityConnector().initialize(application)
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
        when (nextAction?.type) {
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
                listener.onCompleted(AirwallexPaymentStatus.Success(paymentIntentId, consentId))
            }
        }
    }

    override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode == ThreeDSecurityActivityLaunch.REQUEST_CODE) {
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
        sessionId: String,
        securityTokenListener: SecurityTokenListener
    ) {
        AirwallexSecurityConnector().retrieveSecurityToken(
            sessionId,
            securityTokenListener
        )
    }
}
