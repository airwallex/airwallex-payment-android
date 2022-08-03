package com.airwallex.android.core

import android.os.Build
import com.airwallex.android.core.Airwallex.PaymentListener
import com.airwallex.android.core.model.*

interface PaymentManager {

    fun <T> startOperation(options: Options, listener: PaymentListener<T>)

    suspend fun <T> startOperation(options: Options): T?

    companion object {
        private const val PLATFORM = "Android"
        private const val DEVICE_MODEL = "mobile"

        fun buildDeviceInfo(deviceId: String): Device {
            return Device.Builder()
                .setDeviceId(deviceId)
                .setDeviceModel(DEVICE_MODEL)
                .setPlatformType(PLATFORM)
                .setDeviceOS(Build.VERSION.RELEASE)
                .build()
        }
    }
}
