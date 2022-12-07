package com.airwallex.android.core.model

import android.os.Build
import io.mockk.mockkStatic
import org.junit.Test
import kotlin.test.assertEquals

class DeviceTest {

    private val device by lazy {
        Device.Builder()
            .setDeviceId("123456")
            .setDeviceModel("Mate30 pro")
            .setOsType("android")
            .setOsVersion("10.0")
            .build()
    }

    @Test
    fun testParams() {
        assertEquals("123456", device.deviceId)
        assertEquals("Mate30 pro", device.deviceModel)
        assertEquals("android", device.osType)
        assertEquals("10.0", device.version)
    }

    @Test
    fun testToParamsMap() {
        mockkStatic(Build::class)
        val cardParamMap = device.toParamMap()
        assertEquals(
            mapOf(
                "device_id" to "123456",
                "mobile" to mapOf(
                    "device_model" to "Mate30 pro",
                    "os_type" to "android",
                    "os_version" to "10.0"
                )
            ),
            cardParamMap
        )
    }
}
