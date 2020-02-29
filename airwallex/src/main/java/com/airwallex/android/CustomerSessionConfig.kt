package com.airwallex.android

import android.os.Parcelable
import com.airwallex.android.model.PaymentIntent
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomerSessionConfig(
    val paymentIntent: PaymentIntent,
    val token: String
) : Parcelable