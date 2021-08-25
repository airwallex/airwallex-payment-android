package com.airwallex.android.core

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import com.airwallex.android.core.model.Device
import java.math.BigDecimal

class CardNextActionModel(
    val fragment: Fragment?,
    val activity: Activity,
    val applicationContext: Context,
    val paymentManager: PaymentManager,
    val clientSecret: String,
    val device: Device?,
    val paymentIntentId: String,
    val currency: String,
    val amount: BigDecimal,
)
