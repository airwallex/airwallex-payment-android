package com.airwallex.android.googlepay

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.airwallex.android.core.ActionComponent
import com.airwallex.android.core.ActionComponentProvider
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.CardNextActionModel
import com.airwallex.android.core.SecurityTokenListener
import com.airwallex.android.core.model.NextAction

class GooglePayComponent : ActionComponent {
    companion object {
        val PROVIDER: ActionComponentProvider<GooglePayComponent> = GooglePayComponentProvider()
    }

    override fun handlePaymentIntentResponse(
        paymentIntentId: String,
        nextAction: NextAction?,
        activity: Activity,
        applicationContext: Context,
        cardNextActionModel: CardNextActionModel?,
        listener: Airwallex.PaymentResultListener
    ) {
        TODO("Not yet implemented")
    }

    override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        TODO("Not yet implemented")
    }

    override fun retrieveSecurityToken(
        paymentIntentId: String,
        applicationContext: Context,
        securityTokenListener: SecurityTokenListener
    ) {
        // Since only card payments require a device ID, this will not be executed
        securityTokenListener.onResponse("")
    }
}
