package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Billing internal constructor(

    @SerializedName("address")
    val address: Address,

    @SerializedName("date_of_birth")
    val dateOfBirth: Date,

    @SerializedName("email")
    val email: String,

    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String,

    @SerializedName("phone_number")
    val phoneNumber: String
) : AirwallexModel, Parcelable