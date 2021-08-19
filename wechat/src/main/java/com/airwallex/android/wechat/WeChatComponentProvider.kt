package com.airwallex.android.wechat

import android.content.Context
import android.content.Intent
import com.airwallex.android.core.*
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.model.*

class WeChatComponentProvider : ActionComponentProvider<WeChatComponent> {

    override fun handlePaymentIntentResponse(
        nextAction: NextAction?,
        cardNextActionModel: CardNextActionModel?,
        listener: Airwallex.PaymentListener<PaymentIntent>
    ) {
        val nextActionData = nextAction?.data
        if (nextAction == null ||
            nextAction.type != NextAction.NextActionType.CALL_SDK ||
            nextActionData == null
        ) {
            listener.onFailed(AirwallexCheckoutException(message = "Server error, WeChat data is null"))
            return
        }
        listener.onNextActionWithWeChatPay(
            WeChat(
                appId = nextActionData["appId"] as? String,
                partnerId = nextActionData["partnerId"] as? String,
                prepayId = nextActionData["prepayId"] as? String,
                `package` = nextActionData["package"] as? String,
                nonceStr = nextActionData["nonceStr"] as? String,
                timestamp = nextActionData["timeStamp"] as? String,
                sign = nextActionData["sign"] as? String
            )
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return false
    }

    override fun canHandleAction(paymentMethodType: PaymentMethodType): Boolean {
        return paymentMethodType == PaymentMethodType.WECHAT
    }

    override fun retrieveSecurityToken(
        paymentIntentId: String,
        applicationContext: Context,
        securityTokenListener: SecurityTokenListener
    ) {
        securityTokenListener.onResponse("")
    }
}
