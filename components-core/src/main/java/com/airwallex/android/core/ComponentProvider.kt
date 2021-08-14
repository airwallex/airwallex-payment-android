package com.airwallex.android.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.airwallex.android.core.model.*
import java.math.BigDecimal

interface ComponentProvider<T> {

    class CardNextActionModel(
        val fragment: Fragment?,
        val activity: Activity,
        val paymentManager: PaymentManager,
        val clientSecret: String,
        val device: Device?,
        val paymentIntentId: String,
        val currency: String,
        val amount: BigDecimal,
    )

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
