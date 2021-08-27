package com.airwallex.android.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.airwallex.android.core.model.NextAction

interface ActionComponent {

    fun handlePaymentIntentResponse(
        paymentIntentId: String,
        nextAction: NextAction?,
        activity: Activity,
        applicationContext: Context,
        cardNextActionModel: CardNextActionModel?,
        listener: Airwallex.PaymentListener<String>
    )

    fun handleActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ): Boolean
}
