package com.airwallex.android.redirect

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.airwallex.android.core.*
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.extension.putIfNotNull
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.NextAction
import com.airwallex.android.redirect.exception.RedirectException
import com.airwallex.android.redirect.util.RedirectUtil

class RedirectComponent : ActionComponent {

    companion object {
        val PROVIDER: ActionComponentProvider<RedirectComponent> = RedirectComponentProvider()
    }

    override fun initialize(application: Application) {

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
                    RedirectUtil.makeRedirect(activity = activity, redirectUrl = redirectUrl, packageName = nextAction.packageName)
                    listener.onCompleted(AirwallexPaymentStatus.InProgress(paymentIntentId))
                    AnalyticsLogger.logPageView("payment_redirect", mapOf("url" to redirectUrl))
                } catch (e: RedirectException) {
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

    override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return false
    }

    override fun retrieveSecurityToken(
        paymentIntentId: String,
        applicationContext: Context,
        securityTokenListener: SecurityTokenListener
    ) {
        // Since only card payments require a device ID, this will not be executed
        securityTokenListener.onResponse("")
    }
}
