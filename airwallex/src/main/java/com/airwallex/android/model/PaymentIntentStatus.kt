package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class PaymentIntentStatus : Parcelable {

    @SerializedName("SUCCEEDED")
    SUCCEEDED,

    @SerializedName("REQUIRES_MERCHANT_ACTION")
    REQUIRES_MERCHANT_ACTION,

    @SerializedName("REQUIRES_CUSTOMER_ACTION")
    REQUIRES_CUSTOMER_ACTION,

    @SerializedName("REQUIRES_PAYMENT_METHOD")
    REQUIRES_PAYMENT_METHOD
}
