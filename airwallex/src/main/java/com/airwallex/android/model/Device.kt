package com.airwallex.android.model

import android.os.Parcelable
import com.airwallex.android.model.parser.DeviceParser
import kotlinx.parcelize.Parcelize

/**
 * Device info.
 */
@Parcelize
data class Device internal constructor(

    val deviceId: String? = null,
    val deviceModel: String? = null,
    val sdkVersion: String? = null,
    val platformType: String? = null,
    val deviceOS: String? = null
) : AirwallexModel, AirwallexRequestModel, Parcelable {

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>()
            .plus(
                deviceId?.let {
                    mapOf(DeviceParser.FIELD_DEVICE_ID to it)
                }.orEmpty()
            )
            .plus(
                deviceModel?.let {
                    mapOf(DeviceParser.FIELD_DEVICE_MODEL to it)
                }.orEmpty()
            )
            .plus(
                sdkVersion?.let {
                    mapOf(DeviceParser.FIELD_SDK_VERSION to it)
                }.orEmpty()
            )
            .plus(
                platformType?.let {
                    mapOf(DeviceParser.FIELD_PLATFORM_TYPE to it)
                }.orEmpty()
            )
            .plus(
                deviceOS?.let {
                    mapOf(DeviceParser.FIELD_DEVICE_OS to it)
                }.orEmpty()
            )
    }

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
