package com.airwallex.android.core.data

import androidx.activity.ComponentActivity
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.model.PaymentMethod

data class AirwallexCheckoutParam(
    val activity: ComponentActivity,
    val paymentMethod: PaymentMethod,
    val session: AirwallexSession,
    val paymentConsentId: String?,
)
