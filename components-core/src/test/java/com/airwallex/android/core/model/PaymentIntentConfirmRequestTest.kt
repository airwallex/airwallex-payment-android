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
        .setPaymentConsentReference(null)
        .setReturnUrl("https://www.airwallex.com")
        .build()

    @Test
    fun testParams() {
        assertEquals("aaaa", request.requestId)
        assertEquals("111", request.customerId)
        assertEquals(null, request.paymentMethodRequest)
        assertEquals(null, request.paymentMethodOptions)
        assertEquals(null, request.paymentConsentReference)
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
