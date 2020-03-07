package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ThreeDSecureLookup internal constructor(

    @SerializedName("transactionId")
    val transactionId: String? = null,

    @SerializedName("payload")
    val payload: String? = null

) : Parcelable
