package com.airwallex.android

import android.annotation.SuppressLint
import android.provider.Settings
import com.airwallex.android.model.Device

@SuppressLint("HardwareIds")
internal object DeviceUtils {

    // TODO Device_id should use the CyberSource fingerprint
    internal val device by lazy {
        Device.Builder()
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
}
