package com.airwallex.android.core

import com.airwallex.android.core.model.Device
import java.math.BigDecimal

class CardNextActionModel(
    val paymentManager: PaymentManager,
    val clientSecret: String,
    val device: Device?,
    val paymentIntentId: String?,
    val currency: String,
    val amount: BigDecimal,
)
