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
    fun builderConstructor() {
        assertEquals(device, DeviceFixtures.DEVICE)
    }

    @Test
    fun testParams() {
        assertEquals("123456", device.deviceId)
        assertEquals("Mate30 pro", device.deviceModel)
        assertEquals("android", device.deviceOS)
        assertEquals("10.0", device.sdkVersion)
        assertEquals("huawei", device.platformType)
    }
}
