package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Device info.
 */
@Parcelize
data class Device internal constructor(
    val deviceId: String? = null,
    val deviceModel: String? = null,
    val version: String? = null,
    val platformType: String? = null,
    val deviceOS: String? = null
) : AirwallexModel, AirwallexRequestModel, Parcelable {

    companion object {
        const val FIELD_DEVICE_ID = "device_id"
        const val FIELD_DEVICE_MODEL = "device_model"
        const val FIELD_SDK_VERSION = "sdk_version"
        const val FIELD_PLATFORM_TYPE = "platform_type"
        const val FIELD_DEVICE_OS = "device_os"
    }

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>()
            .plus(
                mapOf(FIELD_SDK_VERSION to (version ?: sdkVersion))
            )
            .plus(
                deviceId?.let {
                    mapOf(FIELD_DEVICE_ID to it)
                }.orEmpty()
            )
            .plus(
                deviceModel?.let {
                    mapOf(FIELD_DEVICE_MODEL to it)
                }.orEmpty()
            )
            .plus(
                platformType?.let {
                    mapOf(FIELD_PLATFORM_TYPE to it)
                }.orEmpty()
            )
            .plus(
                deviceOS?.let {
                    mapOf(FIELD_DEVICE_OS to it)
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
                version = sdkVersion,
                platformType = platformType,
                deviceOS = deviceOS
            )
        }
    }
}
