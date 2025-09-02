package com.airwallex.android.card

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.airwallex.android.card.view.cvc.PaymentCheckoutActivityLaunch
import com.airwallex.android.core.*
import com.airwallex.android.core.data.AirwallexCheckoutParam
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.model.*
import com.airwallex.android.threedsecurity.ThreeDSecurityActivityLaunch
import com.airwallex.android.threedsecurity.ThreeDSecurityManager
import com.airwallex.android.ui.AirwallexActivityLaunch

class CardComponent : ActionComponent {

    companion object {
        val PROVIDER: ActionComponentProvider<CardComponent> = CardComponentProvider()
    }

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
        consentId: String?,
    ) {
        if (cardNextActionModel == null) {
            listener.onCompleted(AirwallexPaymentStatus.Failure(AirwallexCheckoutException(message = "Card payment info not found")))
            return
        }
        when (nextAction?.type) {
            // 3DS flow
            NextAction.NextActionType.REDIRECT_FORM -> {
                if (paymentIntentId.isNullOrEmpty()) {
                    listener.onCompleted(
                        AirwallexPaymentStatus.Failure(
                            AirwallexCheckoutException(
                                message = "Payment Intent Id is null or empty"
                            )
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
                    listener = listener,
                    paymentConsentId = consentId
                )
            }
            // payPayment
            else -> {
                listener.onCompleted(AirwallexPaymentStatus.Success(paymentIntentId, consentId))
            }
        }
    }

    override fun <T, R> handlePaymentData(param: T?, callBack: (result: R?) -> Unit) {
        if (param is AirwallexCheckoutParam) {
            PaymentCheckoutActivityLaunch(param.activity)
                .launchForResult(
                    PaymentCheckoutActivityLaunch.Args.Builder()
                        .setAirwallexSession(param.session)
                        .setPaymentMethod(param.paymentMethod)
                        .setPaymentConsentId(param.paymentConsentId)
                        .build()
                ) { _, result ->
                    val paymentStatus = handleCVCActivityResult(
                        param.paymentConsentId,
                        result.resultCode,
                        result.data
                    )
                    @Suppress("UNCHECKED_CAST")
                    callBack(paymentStatus as R)
                }
        }
    }

    override fun handleActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        listener: Airwallex.PaymentResultListener?
    ): Boolean {
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

    private fun handleCVCActivityResult(
        paymentConsentId: String?,
        resultCode: Int,
        data: Intent?
    ): AirwallexPaymentStatus {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val result = PaymentCheckoutActivityLaunch.Result.fromIntent(data)
                when {
                    result == null -> return AirwallexPaymentStatus.Failure(
                        AirwallexCheckoutException(message = "cvc result is null")
                    )

                    result.exception != null -> return AirwallexPaymentStatus.Failure(result.exception)
                    result.paymentIntentId != null -> return AirwallexPaymentStatus.Success(
                        result.paymentIntentId,
                        paymentConsentId
                    )
                }
            }

            Activity.RESULT_CANCELED -> {
                return AirwallexPaymentStatus.Cancel
            }
        }
        return AirwallexPaymentStatus.Failure(AirwallexCheckoutException(message = "unknown cvc error"))
    }
}
