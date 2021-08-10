package com.airwallex.android.core.model.parser

import com.airwallex.android.core.model.Device
import com.airwallex.android.core.util.AirwallexJsonUtils
import org.json.JSONObject

class DeviceParser : ModelJsonParser<Device> {

    override fun parse(json: JSONObject): Device? {
        return Device(
            deviceId = AirwallexJsonUtils.optString(json, FIELD_DEVICE_ID),
            deviceModel = AirwallexJsonUtils.optString(json, FIELD_DEVICE_MODEL),
            sdkVersion = AirwallexJsonUtils.optString(json, FIELD_SDK_VERSION),
            platformType = AirwallexJsonUtils.optString(json, FIELD_PLATFORM_TYPE),
            deviceOS = AirwallexJsonUtils.optString(json, FIELD_DEVICE_OS)
        )
    }

    companion object {
        const val FIELD_DEVICE_ID = "device_id"
        const val FIELD_DEVICE_MODEL = "device_model"
        const val FIELD_SDK_VERSION = "sdk_version"
        const val FIELD_PLATFORM_TYPE = "platform_type"
        const val FIELD_DEVICE_OS = "device_os"
    }
}
