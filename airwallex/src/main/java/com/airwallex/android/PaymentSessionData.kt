package com.airwallex.android

import android.os.Parcelable
import com.airwallex.android.model.PaymentMethod
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentSessionData(
    val config: PaymentSessionConfig? = null,
    val clientSecret: String,
    val token: String,
    val customerId: String,
    val paymentMethod: PaymentMethod? = null
) : Parcelable