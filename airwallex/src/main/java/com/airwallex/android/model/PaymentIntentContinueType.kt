package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class PaymentIntentContinueType : Parcelable {

    @SerializedName("3dsCheckEnrollment")
    ENROLLMENT,

    @SerializedName("3dsValidate")
    VALIDATE
}
