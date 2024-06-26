package com.airwallex.android.googlepay

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.airwallex.android.core.ActionComponent
import com.airwallex.android.core.ActionComponentProvider
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.CardNextActionModel
import com.airwallex.android.core.SecurityTokenListener
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.NextAction
import com.airwallex.android.threedsecurity.AirwallexSecurityConnector
import com.airwallex.android.threedsecurity.ThreeDSecurityActivityLaunch
import com.airwallex.android.threedsecurity.ThreeDSecurityManager

class GooglePayComponent : ActionComponent {
    companion object {
        val PROVIDER: ActionComponentProvider<GooglePayComponent> = GooglePayComponentProvider()
    }

    private var listener: Airwallex.PaymentResultListener? = null
    private var paymentIntentId: String? = null
    lateinit var paymentMethodType: AvailablePaymentMethodType
    lateinit var session: AirwallexSession

    override fun handlePaymentIntentResponse(
        paymentIntentId: String,
        nextAction: NextAction?,
        fragment: Fragment?,
        activity: Activity,
        applicationContext: Context,
        cardNextActionModel: CardNextActionModel?,
        listener: Airwallex.PaymentResultListener
    ) {
        this.listener = listener
        if (nextAction?.type == NextAction.NextActionType.REDIRECT_FORM) {
            if (cardNextActionModel == null) {
                listener.onCompleted(
                    AirwallexPaymentStatus.Failure(
                        AirwallexCheckoutException(message = "Card payment info not found")
                    )
                )
                return
            }
            ThreeDSecurityManager.handleThreeDSFlow(
                paymentIntentId = paymentIntentId,
                activity = activity,
                fragment = fragment,
                nextAction = nextAction,
                cardNextActionModel = cardNextActionModel,
                listener = listener
            )
        } else {
            this.paymentIntentId = paymentIntentId
            val googlePayOptions = session.googlePayOptions ?: return
            val googlePayActivityLaunch = if (fragment != null) {
                GooglePayActivityLaunch(fragment)
            } else {
                GooglePayActivityLaunch(activity)
            }
            googlePayActivityLaunch.startForResult(
                GooglePayActivityLaunch.Args(
                    session = session,
                    googlePayOptions = googlePayOptions,
                    paymentMethodType = paymentMethodType
                )
            )
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
        } else if (requestCode == GooglePayActivityLaunch.REQUEST_CODE) {
            when (val result = GooglePayActivityLaunch.Result.fromIntent(data)) {
                GooglePayActivityLaunch.Result.Cancel -> listener?.onCompleted(
                    AirwallexPaymentStatus.Cancel
                )

                is GooglePayActivityLaunch.Result.Failure -> listener?.onCompleted(
                    AirwallexPaymentStatus.Failure(result.exception)
                )

                is GooglePayActivityLaunch.Result.Success ->
                    paymentIntentId?.let {
                        listener?.onCompleted(AirwallexPaymentStatus.Success(it, result.info))
                    }

                else -> {
                    // no op
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
}
