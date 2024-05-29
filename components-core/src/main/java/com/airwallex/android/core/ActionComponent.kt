package com.airwallex.android.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.airwallex.android.core.model.NextAction

interface ActionComponent {

    @Suppress("LongParameterList")
    fun handlePaymentIntentResponse(
        paymentIntentId: String,
        nextAction: NextAction?,
        fragment: Fragment? = null,
        activity: Activity,
        applicationContext: Context,
        cardNextActionModel: CardNextActionModel?,
        listener: Airwallex.PaymentResultListener
    )

    fun handleActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ): Boolean

    fun retrieveSecurityToken(
        paymentIntentId: String,
        applicationContext: Context,
        securityTokenListener: SecurityTokenListener
    )
}
