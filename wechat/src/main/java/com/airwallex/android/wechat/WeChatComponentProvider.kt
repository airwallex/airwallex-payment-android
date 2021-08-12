package com.airwallex.android.wechat

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.ComponentProvider
import com.airwallex.android.core.PaymentManager
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.model.*
import java.math.BigDecimal

@Suppress("unused")
class WeChatComponentProvider internal constructor(
    private val applicationContext: Context,
    private val paymentManager: PaymentManager
) : ComponentProvider {

    constructor(fragment: Fragment, paymentManager: PaymentManager) : this(
        fragment.requireContext().applicationContext,
        paymentManager
    )

    constructor(activity: Activity, paymentManager: PaymentManager) : this(
        activity.applicationContext,
        paymentManager
    )

    override fun handlePaymentIntentResponse(
        clientSecret: String,
        nextAction: NextAction?,
        device: Device?,
        paymentIntentId: String,
        currency: String,
        amount: BigDecimal,
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
}
