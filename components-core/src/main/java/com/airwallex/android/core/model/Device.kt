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
    val osType: String? = null
) : AirwallexModel, AirwallexRequestModel, Parcelable {

    companion object {
        private const val FIELD_DEVICE_ID = "device_id"
        private const val FIELD_MOBILE = "mobile"
        private const val FIELD_DEVICE_MODEL = "device_model"
        private const val FIELD_OS_TYPE = "os_type"
        private const val FIELD_OS_VERSION = "os_version"
    }

    override fun toParamMap(): Map<String, Any> {
        return mutableMapOf<String, Any>().apply {
            deviceId?.let {
                put(FIELD_DEVICE_ID, it)
            }
            getMobileMap()?.let {
                put(FIELD_MOBILE, it)
            }
        }
    }

    private fun getMobileMap(): Map<String, String>? {
        return mutableMapOf<String, String>().apply {
            deviceModel?.let {
                put(FIELD_DEVICE_MODEL, it)
            }
            version?.let {
                put(FIELD_OS_VERSION, it)
            }
            osType?.let {
                put(FIELD_OS_TYPE, it)
            }
        }.ifEmpty { null }
    }

    class Builder : ObjectBuilder<Device> {
        private var deviceId: String? = null
        private var deviceModel: String? = null
        private var version: String? = null
        private var osType: String? = null

        fun setDeviceId(deviceId: String?): Builder = apply {
            this.deviceId = deviceId
        }

        fun setDeviceModel(deviceModel: String?): Builder = apply {
            this.deviceModel = deviceModel
        }

        fun setOsVersion(osVersion: String?): Builder = apply {
            this.version = osVersion
        }

        fun setOsType(osType: String?): Builder = apply {
            this.osType = osType
        }

        override fun build(): Device {
            return Device(
                deviceId = deviceId,
                deviceModel = deviceModel,
                version = version,
                osType = osType
            )
        }
    }
}
