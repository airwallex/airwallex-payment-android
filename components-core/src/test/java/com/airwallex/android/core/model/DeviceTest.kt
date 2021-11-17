package com.airwallex.android.core.model

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class DeviceTest {

    private val device by lazy {
        Device.Builder()
            .setDeviceId("123456")
            .setDeviceModel("Mate30 pro")
            .setDeviceOS("android")
            .setSdkVersion("10.0")
            .setPlatformType("huawei")
            .build()
    }

    @Test
    fun testParams() {
        assertEquals("123456", device.deviceId)
        assertEquals("Mate30 pro", device.deviceModel)
        assertEquals("android", device.deviceOS)
        assertEquals("10.0", device.version)
        assertEquals("huawei", device.platformType)
    }

    @Test
    fun testToParamsMap() {
        val cardParamMap = device.toParamMap()
        assertEquals(
            mapOf(
                "device_id" to "123456",
                "device_model" to "Mate30 pro",
                "sdk_version" to "10.0",
                "platform_type" to "huawei",
                "device_os" to "android"
            ),
            cardParamMap
        )
    }
}
