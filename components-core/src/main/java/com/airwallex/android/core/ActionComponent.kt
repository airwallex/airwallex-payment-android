package com.airwallex.android.core

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.airwallex.android.core.model.NextAction

interface ActionComponent {

    fun initialize(application: Application) = Unit

    @Suppress("LongParameterList")
    fun handlePaymentIntentResponse(
        paymentIntentId: String?,
        nextAction: NextAction?,
        fragment: Fragment? = null,
        activity: Activity,
        applicationContext: Context,
        cardNextActionModel: CardNextActionModel?,
        listener: Airwallex.PaymentResultListener,
        consentId: String? = null,
    )

    fun <T, R> handlePaymentData(param: T?, callBack: (result: R?) -> Unit) = Unit

    fun handleActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        listener: Airwallex.PaymentResultListener? = null
    ): Boolean
}
