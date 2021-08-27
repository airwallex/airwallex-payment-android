package com.airwallex.android.redirect

import android.content.Context
import com.airwallex.android.core.*
import com.airwallex.android.core.model.*

class RedirectComponentProvider : ActionComponentProvider<RedirectComponent> {

    private val redirectComponent: RedirectComponent by lazy {
        RedirectComponent()
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

    override fun get(): RedirectComponent {
        return redirectComponent
    }
}
