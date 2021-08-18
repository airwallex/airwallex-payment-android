package com.airwallex.android.core.model

import org.junit.Test
import kotlin.test.assertEquals

class PaymentIntentConfirmRequestTest {

    private val request = PaymentIntentConfirmRequest.Builder(
        requestId = "aaaa"
    )
        .setCustomerId("111")
        .setDevice(
            Device.Builder()
                .setDeviceId("123456")
                .setDeviceModel("Mate30 pro")
                .setDeviceOS("android")
                .setSdkVersion("10.0")
                .setPlatformType("huawei")
                .build()
        )
        .setPaymentMethodRequest(null)
        .setPaymentMethodOptions(null)
        .setPaymentConsentReference(
            PaymentConsentReference.Builder()
                .setId("cst_hkdmr7v9rg1j5g4azy6")
                .setCvc("123")
                .build()
        )
        .setReturnUrl("https://www.airwallex.com")
        .build()

    @Test
    fun testParams() {
        assertEquals("aaaa", request.requestId)
        assertEquals("111", request.customerId)
        assertEquals(null, request.paymentMethodRequest)
        assertEquals(null, request.paymentMethodOptions)
        assertEquals(
            PaymentConsentReference.Builder()
                .setId("cst_hkdmr7v9rg1j5g4azy6")
                .setCvc("123")
                .build()
                .toString(),
            request.paymentConsentReference.toString()
        )
        assertEquals(
            Device.Builder()
                .setDeviceId("123456")
                .setDeviceModel("Mate30 pro")
                .setDeviceOS("android")
                .setSdkVersion("10.0")
                .setPlatformType("huawei")
                .build().toParamMap(),
            request.device!!.toParamMap()
        )
        assertEquals("https://www.airwallex.com", request.returnUrl)
    }

    @Test
    fun testToParamsMap() {
        val paramMap = request.toParamMap()
        assertEquals(
            mapOf(
                "request_id" to "aaaa",
                "customer_id" to "111",
                "payment_consent_reference" to mapOf(
                    "id" to "cst_hkdmr7v9rg1j5g4azy6",
                    "cvc" to "123",
                ),
                "device" to mapOf(
                    "device_id" to "123456",
                    "device_model" to "Mate30 pro",
                    "sdk_version" to "10.0",
                    "platform_type" to "huawei",
                    "device_os" to "android"
                ),
                "return_url" to "https://www.airwallex.com"
            ),
            paramMap
        )
    }
}
