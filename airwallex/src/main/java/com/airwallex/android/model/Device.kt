package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Device info.
 */
@Parcelize
data class Device internal constructor(

    @SerializedName("device_id")
    val deviceId: String? = null,

    @SerializedName("device_model")
    val deviceModel: String? = null,

    @SerializedName("sdk_version")
    val sdkVersion: String? = null,

    @SerializedName("platform_type")
    val platformType: String? = null,

    @SerializedName("device_os")
    val deviceOS: String? = null
) : AirwallexModel, Parcelable {
    class Builder : ObjectBuilder<Device> {
        private var deviceId: String? = null
        private var deviceModel: String? = null
        private var sdkVersion: String? = null
        private var platformType: String? = null
        private var deviceOS: String? = null

        fun setDeviceId(deviceId: String?): Builder = apply {
            this.deviceId = deviceId
        }

        fun setDeviceModel(deviceModel: String?): Builder = apply {
            this.deviceModel = deviceModel
        }

        fun setSdkVersion(sdkVersion: String?): Builder = apply {
            this.sdkVersion = sdkVersion
        }

        fun setPlatformType(platformType: String?): Builder = apply {
            this.platformType = platformType
        }

        fun setDeviceOS(deviceOS: String?): Builder = apply {
            this.deviceOS = deviceOS
        }

        override fun build(): Device {
            return Device(
                deviceId = deviceId,
                deviceModel = deviceModel,
                sdkVersion = sdkVersion,
                platformType = platformType,
                deviceOS = deviceOS
            )
        }
    }
}
