package com.airwallex.android.redirect

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.airwallex.android.core.ActionComponent
import com.airwallex.android.core.ActionComponentProvider
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.CardNextActionModel
import com.airwallex.android.core.exception.AirwallexCheckoutException
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
        activity: Activity,
        applicationContext: Context,
        cardNextActionModel: CardNextActionModel?,
        listener: Airwallex.PaymentListener<String>
    ) {
        when (nextAction?.type) {
            NextAction.NextActionType.REDIRECT -> {
                val redirectUrl = nextAction.url
                if (redirectUrl.isNullOrEmpty()) {
                    listener.onFailed(AirwallexCheckoutException(message = "Redirect url not found"))
                    return
                }
                try {
                    listener.onSuccess(paymentIntentId)
                    RedirectUtil.makeRedirect(activity = activity, redirectUrl = redirectUrl)
                } catch (e: RedirectException) {
                    listener.onFailed(e)
                }
            }
            else -> {
                listener.onFailed(AirwallexCheckoutException(message = "Unsupported next action ${nextAction?.type}"))
            }
        }
    }

    override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return false
    }
}
