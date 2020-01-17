package com.airwallex.android.model

import android.os.Parcelable
import androidx.annotation.IntRange
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Device internal constructor(

    @SerializedName("browser_info")
    val browserInfo: String,

    @SerializedName("cookies_accepted")
    val cookiesAccepted: String,

    @SerializedName("device_id")
    val deviceId: String,

    @SerializedName("host_name")
    val hostName: String,

    @SerializedName("http_browser_email")
    val httpBrowserEmail: String,

    @SerializedName("http_browser_type")
    val httpBrowserType: String,

    @SerializedName("ip_address")
    val ipAddress: String,

    @SerializedName("ip_network_address")
    val ipNetworkAddress: String

) : AirwallexModel, Parcelable