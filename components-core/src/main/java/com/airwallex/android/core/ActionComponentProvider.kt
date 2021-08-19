package com.airwallex.android.core

import android.content.Context
import android.content.Intent
import com.airwallex.android.core.model.NextAction
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PaymentMethodType

interface ActionComponentProvider<ComponentT : ActionComponent?> {

    fun canHandleAction(paymentMethodType: PaymentMethodType): Boolean

    fun retrieveSecurityToken(
        paymentIntentId: String,
        applicationContext: Context,
        securityTokenListener: SecurityTokenListener
    )

    fun handlePaymentIntentResponse(
        nextAction: NextAction?,
        cardNextActionModel: CardNextActionModel?,
        listener: Airwallex.PaymentListener<PaymentIntent>
    )

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean
}
