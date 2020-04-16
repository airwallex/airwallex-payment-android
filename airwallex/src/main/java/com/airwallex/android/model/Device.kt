package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Device internal constructor(


    @SerializedName("device_id")
    val deviceId: String? = null,

    @SerializedName("host_name")
    val hostName: String? = null,

    @SerializedName("cookies_accepted")
    val cookiesAccepted: String? = null,

    @SerializedName("http_browser_type")
    val httpBrowserType: String? = null
) : AirwallexModel, Parcelable {
    class Builder : ObjectBuilder<Device> {
        private var cookiesAccepted: String? = null
        private var deviceId: String? = null
        private var hostName: String? = null
        private var httpBrowserType: String? = null

        fun setCookiesAccepted(cookiesAccepted: String?): Builder = apply {
            this.cookiesAccepted = cookiesAccepted
        }

        fun setDeviceId(deviceId: String?): Builder = apply {
            this.deviceId = deviceId
        }

        fun setHostName(hostName: String?): Builder = apply {
            this.hostName = hostName
        }

        fun setHttpBrowserType(httpBrowserType: String?): Builder = apply {
            this.httpBrowserType = httpBrowserType
        }

        override fun build(): Device {
            return Device(
                cookiesAccepted = cookiesAccepted,
                deviceId = deviceId,
                hostName = hostName,
                httpBrowserType = httpBrowserType
            )
        }
    }
}
