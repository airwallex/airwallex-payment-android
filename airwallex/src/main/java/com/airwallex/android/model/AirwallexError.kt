package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Airwallex uses conventional HTTP response codes to indicate the success or failure of an API
 * request. Typically a status code in the 2xx range indicates success, status codes in the 4xx
 * range indicate an error that has been triggered due to the information provided (for example;
 * a parameter does not meet the validation requirements or was not provided), and status codes
 * in the 5xx range indicate an error with our servers.
 */
@Parcelize
data class AirwallexError internal constructor(

    @SerializedName("code")
    val code: String? = null,

    @SerializedName("source")
    val source: String? = null,

    @SerializedName("message")
    val message: String? = null
) : AirwallexModel, Parcelable {

    override fun toString(): String {
        return "code $code, source $source, message $message"
    }
}
