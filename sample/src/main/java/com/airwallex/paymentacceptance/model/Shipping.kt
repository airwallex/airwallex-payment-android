package com.airwallex.paymentacceptance.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Shipping(

    @SerializedName("shipping_method")
    var shippingMethod: String,

    @SerializedName("first_name")
    var firstName: String,

    @SerializedName("last_name")
    var lastName: String,

    @SerializedName("phone_number")
    var phone: String,

    @SerializedName("address")
    var address: Address
) : Parcelable
