package com.airwallex.android.core

import android.content.Context
import com.airwallex.android.core.model.PaymentMethodType

interface ActionComponentProvider<Component : ActionComponent?> {

    fun get(): Component

    fun canHandleAction(
        paymentMethodType:
            PaymentMethodType
    ): Boolean

    fun retrieveSecurityToken(
        paymentIntentId: String,
        applicationContext: Context,
        securityTokenListener: SecurityTokenListener
    )
}
