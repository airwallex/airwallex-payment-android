package com.airwallex.android.core.model

import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
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
        assertNull(paramMap["payment_consent"])
    }

    @Test
    fun `test toParamMap with payment consent options - full configuration`() {
        val paymentConsentOptions = PaymentConsentOptions(
            nextTriggeredBy = PaymentConsent.NextTriggeredBy.MERCHANT,
            merchantTriggerReason = PaymentConsent.MerchantTriggerReason.SCHEDULED,
            termsOfUse = PaymentConsentOptions.TermsOfUse(
                paymentAmountType = PaymentConsentOptions.PaymentAmountType.FIXED,
                fixedPaymentAmount = BigDecimal("100.00"),
                paymentCurrency = "USD",
                billingCycleChargeDay = 15,
                startDate = "2024-01-01T00:00:00Z",
                endDate = "2025-12-31T00:00:00Z",
                totalBillingCycles = 12,
                paymentSchedule = PaymentConsentOptions.PaymentSchedule(
                    period = 1,
                    periodUnit = PaymentConsentOptions.PeriodUnit.MONTH,
                )
            )
        )

        val request = PaymentIntentConfirmRequest.Builder("request-123")
            .setCustomerId("customer-456")
            .setPaymentConsent(paymentConsentOptions)
            .build()

        val paramMap = request.toParamMap()

        // Verify payment_consent is in the map
        assertNotNull(paramMap["payment_consent"])
        val consentMap = paramMap["payment_consent"] as Map<*, *>

        // Verify top-level consent fields
        assertEquals("merchant", consentMap["next_triggered_by"])
        assertEquals("scheduled", consentMap["merchant_trigger_reason"])

        // Verify terms_of_use
        assertNotNull(consentMap["terms_of_use"])
        val termsMap = consentMap["terms_of_use"] as Map<*, *>
        assertEquals("FIXED", termsMap["payment_amount_type"])
        // BigDecimal is stored directly in the map
        assertEquals(BigDecimal("100.00"), termsMap["fixed_payment_amount"])
        assertEquals("USD", termsMap["payment_currency"])
        assertEquals(15, termsMap["billing_cycle_charge_day"])
        assertEquals("2025-12-31T00:00:00Z", termsMap["end_date"])
        assertEquals("2024-01-01T00:00:00Z", termsMap["start_date"])
        assertEquals(12, termsMap["total_billing_cycles"])

        // Verify payment_schedule
        assertNotNull(termsMap["payment_schedule"])
        val scheduleMap = termsMap["payment_schedule"] as Map<*, *>
        assertEquals(1, scheduleMap["period"])
        assertEquals("MONTH", scheduleMap["period_unit"])
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
