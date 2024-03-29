package com.airwallex.android.core

import com.airwallex.android.core.Airwallex.PaymentListener
import com.airwallex.android.core.model.*

interface PaymentManager {

    fun <T> startOperation(options: Options, listener: PaymentListener<T>)

    suspend fun retrieveAvailablePaymentConsents(options: Options.RetrieveAvailablePaymentConsentsOptions):
            Page<PaymentConsent>

    suspend fun retrieveAvailablePaymentMethods(options: Options.RetrieveAvailablePaymentMethodsOptions):
            Page<AvailablePaymentMethodType>

    suspend fun createPaymentMethod(options: Options.CreatePaymentMethodOptions):
            PaymentMethod

    suspend fun createPaymentConsent(options: Options.CreatePaymentConsentOptions):
            PaymentConsent

    fun buildDeviceInfo(deviceId: String): Device
}
