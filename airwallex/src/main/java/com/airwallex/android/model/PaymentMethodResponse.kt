package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentMethodResponse internal constructor(
    @SerializedName("has_more")
    val hasMore: Boolean,

    @SerializedName("items")
    val items: List<PaymentMethod>
) : AirwallexModel, Parcelable