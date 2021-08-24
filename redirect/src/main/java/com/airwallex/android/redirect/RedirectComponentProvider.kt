package com.airwallex.android.redirect

import android.content.Context
import android.content.Intent
import com.airwallex.android.core.*
import com.airwallex.android.core.exception.APIException
import com.airwallex.android.core.model.*

class RedirectComponentProvider : ActionComponentProvider<RedirectComponent> {

    override fun handlePaymentIntentResponse(
        nextAction: NextAction?,
        cardNextActionModel: CardNextActionModel?,
        listener: Airwallex.PaymentListener<PaymentIntent>
    ) {
        when (nextAction?.type) {
            NextAction.NextActionType.REDIRECT -> {
                val redirectUrl = nextAction.url
                if (redirectUrl.isNullOrEmpty()) {
                    listener.onFailed(APIException(message = "Server error, redirect url is null"))
                    return
                }
                listener.onNextActionWithRedirectUrl(redirectUrl)
            }
            else -> {
                listener.onFailed(APIException(message = "Unsupported next action"))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return false
    }

    override fun canHandleAction(paymentMethodType: PaymentMethodType): Boolean {
        return paymentMethodType != PaymentMethodType.CARD && paymentMethodType != PaymentMethodType.WECHAT
    }

    override fun retrieveSecurityToken(
        paymentIntentId: String,
        applicationContext: Context,
        securityTokenListener: SecurityTokenListener
    ) {
        securityTokenListener.onResponse("")
    }
}
