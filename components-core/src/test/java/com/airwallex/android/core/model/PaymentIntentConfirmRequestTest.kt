package com.airwallex.android.core.model

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class PaymentIntentConfirmRequestTest {

    private val request = PaymentIntentConfirmRequest.Builder(
        requestId = "3d702b7b-ac7a-46b5-bf62-9fee0dd713bf"
    )
        .setCustomerId("cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu")
        .setDevice(
            Device.Builder()
                .setDeviceId("123456")
                .setDeviceModel("Mate30 pro")
                .setOsType("android")
                .setOsVersion("10.0")
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
                .setDeviceModel("Mate30 pro")
                .setOsType("android")
                .setOsVersion("10.0")
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
                "device_data" to mapOf(
                    "device_id" to "123456",
                    "mobile" to mapOf(
                        "device_model" to "Mate30 pro",
                        "os_type" to "android",
                        "os_version" to "10.0"
                    )
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

    @Test
    fun `test toParamMap excludes customer_id when customerId is empty`() {
        val requestWithEmptyCustomerId = PaymentIntentConfirmRequest.Builder(
            requestId = "test-request-id"
        )
            .setCustomerId("")
            .build()

        val paramMap = requestWithEmptyCustomerId.toParamMap()
        assertFalse(paramMap.containsKey("customer_id"))
    }

    @Test
    fun `test toParamMap excludes customer_id when customerId is null`() {
        val requestWithNullCustomerId = PaymentIntentConfirmRequest.Builder(
            requestId = "test-request-id"
        )
            .setCustomerId(null)
            .build()

        val paramMap = requestWithNullCustomerId.toParamMap()
        assertFalse(paramMap.containsKey("customer_id"))
    }

    @Test
    fun `test toParamMap includes customer_id when customerId is non-empty`() {
        val requestWithCustomerId = PaymentIntentConfirmRequest.Builder(
            requestId = "test-request-id"
        )
            .setCustomerId("cus_123")
            .build()

        val paramMap = requestWithCustomerId.toParamMap()
        assertEquals("cus_123", paramMap["customer_id"])
    }
}
