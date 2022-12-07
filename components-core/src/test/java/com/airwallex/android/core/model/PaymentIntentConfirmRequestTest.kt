package com.airwallex.android.core.model

import org.junit.Test
import kotlin.test.assertEquals

class PaymentIntentConfirmRequestTest {

    private val request = PaymentIntentConfirmRequest.Builder(
        requestId = "3d702b7b-ac7a-46b5-bf62-9fee0dd713bf"
    )
        .setCustomerId("cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu")
        .setDevice(
            Device.Builder()
                .setDeviceId("123456")
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
        .setIntegrationData(
            IntegrationData(
                type = "mobile_sdk",
                version = "10.0"
            )
        )
        .build()

    @Test
    fun testParams() {
        assertEquals("3d702b7b-ac7a-46b5-bf62-9fee0dd713bf", request.requestId)
        assertEquals("cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu", request.customerId)
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
                "request_id" to "3d702b7b-ac7a-46b5-bf62-9fee0dd713bf",
                "customer_id" to "cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu",
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
                "return_url" to "https://www.airwallex.com",
                "integration_data" to mapOf(
                    "type" to "mobile_sdk",
                    "version" to "10.0"
                )
            ),
            paramMap
        )
    }
}
