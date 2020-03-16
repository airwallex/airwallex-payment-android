package com.airwallex.android

import com.airwallex.android.model.Device

internal object DeviceUtils {

    // TODO Device_id should use the CyberSource fingerprint
    internal val device by lazy {
        Device.Builder()
            .setCookiesAccepted("true")
            .setDeviceId("device_id")
            .setHostName("www.airwallex.com")
            .setHttpBrowserType("chrome")
            .build()
    }
}
