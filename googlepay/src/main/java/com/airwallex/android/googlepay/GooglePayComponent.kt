package com.airwallex.android.googlepay

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.fragment.app.Fragment
import com.airwallex.android.core.ActionComponent
import com.airwallex.android.core.ActionComponentProvider
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.CardNextActionModel
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.NextAction
import com.airwallex.android.threedsecurity.ThreeDSecurityActivityLaunch
import com.airwallex.android.threedsecurity.ThreeDSecurityManager
import com.airwallex.android.ui.AirwallexActivityLaunch

class GooglePayComponent : ActionComponent {
    companion object {
        val PROVIDER: ActionComponentProvider<GooglePayComponent> = GooglePayComponentProvider()
    }
    private var paymentIntentId: String? = null
    lateinit var paymentMethodType: AvailablePaymentMethodType
    lateinit var session: AirwallexSession

    override fun initialize(application: Application) {
        AirwallexActivityLaunch.initialize(application)
    }

    override fun handlePaymentIntentResponse(
        paymentIntentId: String?,
        nextAction: NextAction?,
        fragment: Fragment?,
        activity: Activity,
        applicationContext: Context,
        cardNextActionModel: CardNextActionModel?,
        listener: Airwallex.PaymentResultListener,
        consentId: String?
    ) {
        if (paymentIntentId.isNullOrEmpty()) {
            listener.onCompleted(
                AirwallexPaymentStatus.Failure(
                    AirwallexCheckoutException(message = "paymentIntentId is null or empty")
                )
            )
            return
        }
        if (nextAction?.type == NextAction.NextActionType.REDIRECT_FORM) {
            if (cardNextActionModel == null) {
                listener.onCompleted(
                    AirwallexPaymentStatus.Failure(
                        AirwallexCheckoutException(message = "Card payment info not found")
                    )
                )
                return
            }
            AirwallexLogger.info("GooglePayComponent handlePaymentIntentResponse: handleThreeDSFlow")
            ThreeDSecurityManager.handleThreeDSFlow(
                paymentIntentId = paymentIntentId,
                activity = activity,
                fragment = fragment,
                nextAction = nextAction,
                cardNextActionModel = cardNextActionModel,
                listener = listener
            ) { requestCode, resultCode, data ->
                handleActivityResult(requestCode, resultCode, data, listener)
            }
        } else {
            this.paymentIntentId = paymentIntentId
            val googlePayOptions = session.googlePayOptions ?: return
            val googlePayActivityLaunch = if (fragment != null) {
                GooglePayActivityLaunch(fragment)
            } else {
                GooglePayActivityLaunch(activity)
            }
            AirwallexLogger.info("GooglePayComponent handlePaymentIntentResponse: launch googlePay Activity")
            googlePayActivityLaunch.launchForResult(
                GooglePayActivityLaunch.Args(
                    session = session,
                    googlePayOptions = googlePayOptions,
                    paymentMethodType = paymentMethodType
                )
            ) { requestCode: Int, result: ActivityResult ->
                handleActivityResult(requestCode, result.resultCode, result.data, listener)
            }
        }
    }

    override fun handleActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        listener: Airwallex.PaymentResultListener?
    ): Boolean {
        AirwallexLogger.info("GooglePayComponent handleActivityResult: requestCode =$requestCode")
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

                is GooglePayActivityLaunch.Result.Success -> {
                    AirwallexLogger.info("GooglePayComponent handleActivityResult: success, paymentIntentId =$paymentIntentId")
                    listener?.onCompleted(
                        AirwallexPaymentStatus.Success(
                            paymentIntentId ?: "",
                            additionalInfo = result.info
                        )
                    )
                }
                else -> {
                    // no op
                    AirwallexLogger.info("GooglePayComponent handleActivityResult: unknown result")
                }
            }
            return true
        }
        return false
    }
}
