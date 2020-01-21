package com.airwallex.android.model

import com.airwallex.android.ParcelUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class DeviceTest {

    @Test
    fun builderConstructor() {
        val device = Device.Builder()
            .setBrowserInfo("Chrome/76.0.3809.100")
            .setCookiesAccepted("true")
            .setDeviceId("IMEI-4432fsdafd31243244fdsafdfd653")
            .setHostName("www.airwallex.com")
            .setHttpBrowserEmail("jim631@sina.com")
            .setHttpBrowserType("chrome")
            .setIpAddress("123.90.0.1")
            .setIpNetworkAddress("128.0.0.0")
            .build()
        assertEquals(device, DeviceFixtures.DEVICE)
    }

    @Test
    fun testParcelable() {
        assertEquals(DeviceFixtures.DEVICE, ParcelUtils.create(DeviceFixtures.DEVICE))
    }
}