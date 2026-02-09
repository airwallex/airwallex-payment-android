package com.airwallex.android.core

import com.airwallex.android.core.Airwallex.PaymentListener
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.Device
import com.airwallex.android.core.model.Options
import com.airwallex.android.core.model.Page
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethod

interface PaymentManager {

    fun <T> startOperation(options: Options, listener: PaymentListener<T>)

    suspend fun retrieveAvailablePaymentConsents(options: Options.RetrieveAvailablePaymentConsentsOptions):
            Page<PaymentConsent>

    suspend fun retrieveAvailablePaymentMethods(options: Options.RetrieveAvailablePaymentMethodsOptions):
            Page<AvailablePaymentMethodType>

    suspend fun createPaymentMethod(options: Options.CreatePaymentMethodOptions): PaymentMethod

    suspend fun createPaymentConsent(options: Options.CreatePaymentConsentOptions): PaymentConsent

    fun buildDeviceInfo(deviceId: String): Device
}
