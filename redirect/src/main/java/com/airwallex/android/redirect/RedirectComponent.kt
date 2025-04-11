package com.airwallex.android.redirect

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.airwallex.android.core.*
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.extension.putIfNotNull
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.NextAction
import com.airwallex.android.redirect.exception.RedirectException
import com.airwallex.android.redirect.util.RedirectUtil

class RedirectComponent : ActionComponent {

    companion object {
        val PROVIDER: ActionComponentProvider<RedirectComponent> = RedirectComponentProvider()
    }

    override fun handlePaymentIntentResponse(
        paymentIntentId: String,
        nextAction: NextAction?,
        fragment: Fragment?,
        activity: Activity,
        applicationContext: Context,
        cardNextActionModel: CardNextActionModel?,
        listener: Airwallex.PaymentResultListener,
        consentId: String?
    ) {
        when (nextAction?.type) {
            NextAction.NextActionType.REDIRECT -> {
                val redirectUrl = nextAction.url
                if (redirectUrl.isNullOrEmpty()) {
                    AirwallexLogger.error("RedirectComponent handlePaymentIntentResponse: redirectUrl is null or empty")
                    listener.onCompleted(
                        AirwallexPaymentStatus.Failure(
                            AirwallexCheckoutException(
                                message = "Redirect url not found"
                            )
                        )
                    )
                    return
                }
                try {
                    AirwallexLogger.info("RedirectComponent handlePaymentIntentResponse makeRedirect")
                    RedirectUtil.makeRedirect(activity = activity, redirectUrl = redirectUrl, fallBackUrl = nextAction.fallbackUrl, packageName = nextAction.packageName)
                    listener.onCompleted(AirwallexPaymentStatus.InProgress(paymentIntentId))
                    AnalyticsLogger.logPageView("payment_redirect", mapOf("url" to redirectUrl))
                } catch (e: RedirectException) {
                    AirwallexLogger.error("RedirectComponent handlePaymentIntentResponse error: ${e.message}")
                    listener.onCompleted(AirwallexPaymentStatus.Failure(e))
                    AnalyticsLogger.logError(
                        "payment_redirect",
                        mutableMapOf<String, Any>().apply {
                            putIfNotNull("url", redirectUrl)
                            putIfNotNull("message", e.message)
                        }
                    )
                }
            }
            else -> {
                AirwallexLogger.error("RedirectComponent handlePaymentIntentResponse error: unsupported next action ${nextAction?.type}")
                listener.onCompleted(
                    AirwallexPaymentStatus.Failure(
                        AirwallexCheckoutException(
                            message = "Unsupported next action ${nextAction?.type}"
                        )
                    )
                )
            }
        }
    }

    override fun handleActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        listener: Airwallex.PaymentResultListener?
    ): Boolean {
        return false
    }
}
