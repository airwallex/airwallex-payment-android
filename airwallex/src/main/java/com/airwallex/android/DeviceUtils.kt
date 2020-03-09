package com.airwallex.android

import android.annotation.SuppressLint
import android.provider.Settings
import com.airwallex.android.model.Device

internal object DeviceUtils {

    // TODO Device_id should use the CyberSource fingerprint
    @SuppressLint("HardwareIds")
    internal val device = Device.Builder()
        .setCookiesAccepted("true")
        .setDeviceId(
            Settings.Secure.getString(
                ContextProvider.applicationContext.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        )
        .setHostName("www.airwallex.com")
        .setHttpBrowserType("chrome")
        .build()
}
