package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Device internal constructor(

    @SerializedName("browser_info")
    val browserInfo: String? = null,

    @SerializedName("cookies_accepted")
    val cookiesAccepted: String? = null,

    @SerializedName("device_id")
    val deviceId: String? = null,

    @SerializedName("host_name")
    val hostName: String? = null,

    @SerializedName("http_browser_email")
    val httpBrowserEmail: String? = null,

    @SerializedName("http_browser_type")
    val httpBrowserType: String? = null,

    @SerializedName("ip_address")
    val ipAddress: String? = null,

    @SerializedName("ip_network_address")
    val ipNetworkAddress: String? = null

) : AirwallexModel, Parcelable {
    class Builder : ObjectBuilder<Device> {
        private var browserInfo: String? = null
        private var cookiesAccepted: String? = null
        private var deviceId: String? = null
        private var hostName: String? = null
        private var httpBrowserEmail: String? = null
        private var httpBrowserType: String? = null
        private var ipAddress: String? = null
        private var ipNetworkAddress: String? = null

        fun setBrowserInfo(browserInfo: String?): Builder = apply {
            this.browserInfo = browserInfo
        }

        fun setCookiesAccepted(cookiesAccepted: String?): Builder = apply {
            this.cookiesAccepted = cookiesAccepted
        }

        fun setDeviceId(deviceId: String?): Builder = apply {
            this.deviceId = deviceId
        }

        fun setHostName(hostName: String?): Builder = apply {
            this.hostName = hostName
        }

        fun setHttpBrowserEmail(httpBrowserEmail: String?): Builder = apply {
            this.httpBrowserEmail = httpBrowserEmail
        }

        fun setHttpBrowserType(httpBrowserType: String?): Builder = apply {
            this.httpBrowserType = httpBrowserType
        }

        fun setIpAddress(ipAddress: String?): Builder = apply {
            this.ipAddress = ipAddress
        }

        fun setIpNetworkAddress(ipNetworkAddress: String?): Builder = apply {
            this.ipNetworkAddress = ipNetworkAddress
        }

        override fun build(): Device {
            return Device(
                browserInfo = browserInfo,
                cookiesAccepted = cookiesAccepted,
                deviceId = deviceId,
                hostName = hostName,
                httpBrowserEmail = httpBrowserEmail,
                httpBrowserType = httpBrowserType,
                ipAddress = ipAddress,
                ipNetworkAddress = ipNetworkAddress
            )
        }
    }
}
