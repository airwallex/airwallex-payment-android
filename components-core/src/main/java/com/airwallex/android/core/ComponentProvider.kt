package com.airwallex.android.core

import android.content.Intent
import com.airwallex.android.core.model.*
import java.math.BigDecimal

interface ComponentProvider {

    fun handlePaymentIntentResponse(
        clientSecret: String,
        nextAction: NextAction?,
        device: Device?,
        paymentIntentId: String,
        currency: String,
        amount: BigDecimal,
        listener: Airwallex.PaymentListener<PaymentIntent>
    )

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean
}
