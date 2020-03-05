package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AirwallexError internal constructor(

    @SerializedName("code")
    val code: String?,

    @SerializedName("source")
    val source: String?,

    @SerializedName("message")
    val message: String?
) : AirwallexModel, Parcelable {

    override fun toString(): String {
        return "code $code, source $source, message $message"
    }
}