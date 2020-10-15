package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Reference for payment method
 */
@Parcelize
data class PaymentMethodReference internal constructor(

    /**
     * The id of the [PaymentMethod]
     */
    @SerializedName("id")
    val id: String,

    /**
     * The cvc of the card
     */
    @SerializedName("cvc")
    val cvc: String
) : AirwallexModel, Parcelable
