package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AirwallexError internal constructor(

    @SerializedName("message")
    val message: String,

    @SerializedName("path")
    val path: String,

    @SerializedName("error")
    val error: String,

    @SerializedName("status")
    val status: Int,

    @SerializedName("timestamp")
    val timestamp: Long
) : AirwallexModel, Parcelable {

    override fun toString(): String {
        return "message $message, path $path, error $error, status $status, timestamp $timestamp"
    }
}