package com.airwallex.android.wechat

import android.content.Context
import com.airwallex.android.core.*
import com.airwallex.android.core.model.*

class WeChatComponentProvider : ActionComponentProvider<WeChatComponent> {

    val weChatComponent: WeChatComponent by lazy {
        WeChatComponent()
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

    override fun get(): WeChatComponent {
        return weChatComponent
    }
}
