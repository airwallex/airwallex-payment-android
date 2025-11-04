package com.airwallex.android.core

import android.app.Activity
import com.airwallex.android.core.model.Device
import java.math.BigDecimal

@Suppress("LongParameterList")
class CardNextActionModel(
    val paymentManager: PaymentManager,
    val clientSecret: String,
    val device: Device?,
    val paymentIntentId: String?,
    val currency: String,
    val amount: BigDecimal,
    val activityProvider: (() -> Activity)
)