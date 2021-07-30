package com.airwallex.android.model

import com.airwallex.android.model.parser.DeviceParser
import org.json.JSONObject

internal object DeviceFixtures {
    val DEVICE: Device? = DeviceParser().parse(
        JSONObject(
            """
        {
            "device_id": "123456",
            "device_model": "Mate30 pro",
            "sdk_version": "10.0",
            "platform_type": "huawei",
            "device_os": "android"
        }
            """.trimIndent()
        )
    )
}
