package com.airwallex.android.model

import kotlin.test.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DeviceTest {

    @Test
    fun builderConstructor() {
        val device = Device.Builder()
            .setDeviceId("123456")
            .setDeviceModel("Mate30 pro")
            .setDeviceOS("android")
            .setSdkVersion("10.0")
            .setPlatformType("huawei")
            .build()
        assertEquals(device, DeviceFixtures.DEVICE)
    }
}
