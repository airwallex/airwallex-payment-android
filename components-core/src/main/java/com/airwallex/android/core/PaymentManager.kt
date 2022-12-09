package com.airwallex.android.core

import com.airwallex.android.core.Airwallex.PaymentListener
import com.airwallex.android.core.model.*

interface PaymentManager {

    fun <T> startOperation(options: Options, listener: PaymentListener<T>)

    suspend fun startRetrieveAvailablePaymentMethodsOperation(options: Options):
        AvailablePaymentMethodTypeResponse

    fun buildDeviceInfo(deviceId: String): Device
}
