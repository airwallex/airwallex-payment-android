package com.airwallex.android.core.model

import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

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
    fun `test toParamMap with payment consent options - VARIABLE payment type`() {
        val paymentConsentOptions = PaymentConsentOptions(
            nextTriggeredBy = PaymentConsent.NextTriggeredBy.MERCHANT,
            merchantTriggerReason = PaymentConsent.MerchantTriggerReason.UNSCHEDULED,
            termsOfUse = PaymentConsentOptions.TermsOfUse(
                paymentAmountType = PaymentConsentOptions.PaymentAmountType.VARIABLE,
                maxPaymentAmount = BigDecimal("500.00"),
                minPaymentAmount = BigDecimal("10.00"),
                firstPaymentAmount = BigDecimal("50.00"),
                paymentCurrency = "EUR"
            )
        )

        val request = PaymentIntentConfirmRequest.Builder("request-123")
            .setCustomerId("customer-456")
            .setPaymentConsent(paymentConsentOptions)
            .build()

        val paramMap = request.toParamMap()
        val consentMap = paramMap["payment_consent"] as Map<*, *>

        assertEquals("merchant", consentMap["next_triggered_by"])
        assertEquals("unscheduled", consentMap["merchant_trigger_reason"])

        val termsMap = consentMap["terms_of_use"] as Map<*, *>
        assertEquals("VARIABLE", termsMap["payment_amount_type"])
        assertEquals(BigDecimal("500.00"), termsMap["max_payment_amount"])
        assertEquals(BigDecimal("10.00"), termsMap["min_payment_amount"])
        assertEquals(BigDecimal("50.00"), termsMap["first_payment_amount"])
        assertEquals("EUR", termsMap["payment_currency"])
        assertNull(termsMap["fixed_payment_amount"])
        assertNull(termsMap["billing_cycle_charge_day"])
        assertNull(termsMap["payment_schedule"])
    }

    @Test
    fun `test toParamMap with payment consent options - customer triggered`() {
        val paymentConsentOptions = PaymentConsentOptions(
            nextTriggeredBy = PaymentConsent.NextTriggeredBy.CUSTOMER,
            merchantTriggerReason = null,
            termsOfUse = PaymentConsentOptions.TermsOfUse(
                paymentAmountType = PaymentConsentOptions.PaymentAmountType.VARIABLE,
                maxPaymentAmount = BigDecimal("1000.00")
            )
        )

        val request = PaymentIntentConfirmRequest.Builder("request-123")
            .setCustomerId("customer-456")
            .setPaymentConsent(paymentConsentOptions)
            .build()

        val paramMap = request.toParamMap()
        val consentMap = paramMap["payment_consent"] as Map<*, *>

        assertEquals("customer", consentMap["next_triggered_by"])
        assertNull(consentMap["merchant_trigger_reason"])

        val termsMap = consentMap["terms_of_use"] as Map<*, *>
        assertEquals("VARIABLE", termsMap["payment_amount_type"])
        assertEquals(BigDecimal("1000.00"), termsMap["max_payment_amount"])
    }

    @Test
    fun `test toParamMap with payment consent options - minimal configuration`() {
        val paymentConsentOptions = PaymentConsentOptions(
            nextTriggeredBy = PaymentConsent.NextTriggeredBy.CUSTOMER,
            merchantTriggerReason = null,
            termsOfUse = null
        )

        val request = PaymentIntentConfirmRequest.Builder("request-123")
            .setCustomerId("customer-456")
            .setPaymentConsent(paymentConsentOptions)
            .build()

        val paramMap = request.toParamMap()
        val consentMap = paramMap["payment_consent"] as Map<*, *>

        assertEquals("customer", consentMap["next_triggered_by"])
        assertNull(consentMap["merchant_trigger_reason"])
        assertNull(consentMap["terms_of_use"])
    }

    @Test
    fun `test toParamMap with payment consent options - all period units`() {
        val periodUnits = listOf(
            PaymentConsentOptions.PeriodUnit.DAY,
            PaymentConsentOptions.PeriodUnit.WEEK,
            PaymentConsentOptions.PeriodUnit.MONTH,
            PaymentConsentOptions.PeriodUnit.YEAR
        )

        periodUnits.forEach { periodUnit ->
            val paymentConsentOptions = PaymentConsentOptions(
                nextTriggeredBy = PaymentConsent.NextTriggeredBy.MERCHANT,
                merchantTriggerReason = PaymentConsent.MerchantTriggerReason.SCHEDULED,
                termsOfUse = PaymentConsentOptions.TermsOfUse(
                    paymentAmountType = PaymentConsentOptions.PaymentAmountType.FIXED,
                    fixedPaymentAmount = BigDecimal("100.00"),
                    paymentSchedule = PaymentConsentOptions.PaymentSchedule(
                        period = 2,
                        periodUnit = periodUnit
                    )
                )
            )

            val request = PaymentIntentConfirmRequest.Builder("request-123")
                .setCustomerId("customer-456")
                .setPaymentConsent(paymentConsentOptions)
                .build()

            val paramMap = request.toParamMap()
            val consentMap = paramMap["payment_consent"] as Map<*, *>
            val termsMap = consentMap["terms_of_use"] as Map<*, *>
            val scheduleMap = termsMap["payment_schedule"] as Map<*, *>

            assertEquals(2, scheduleMap["period"])
            assertEquals(periodUnit.value, scheduleMap["period_unit"])
        }
    }

    @Test
    fun `test toParamMap with payment consent options - payment schedule with only period`() {
        val paymentConsentOptions = PaymentConsentOptions(
            nextTriggeredBy = PaymentConsent.NextTriggeredBy.MERCHANT,
            merchantTriggerReason = PaymentConsent.MerchantTriggerReason.SCHEDULED,
            termsOfUse = PaymentConsentOptions.TermsOfUse(
                paymentAmountType = PaymentConsentOptions.PaymentAmountType.FIXED,
                fixedPaymentAmount = BigDecimal("100.00"),
                paymentSchedule = PaymentConsentOptions.PaymentSchedule(
                    period = 3,
                    periodUnit = null
                )
            )
        )

        val request = PaymentIntentConfirmRequest.Builder("request-123")
            .setCustomerId("customer-456")
            .setPaymentConsent(paymentConsentOptions)
            .build()

        val paramMap = request.toParamMap()
        val consentMap = paramMap["payment_consent"] as Map<*, *>
        val termsMap = consentMap["terms_of_use"] as Map<*, *>
        val scheduleMap = termsMap["payment_schedule"] as Map<*, *>

        assertEquals(3, scheduleMap["period"])
        assertNull(scheduleMap["period_unit"])
    }

    @Test
    fun `test toParamMap with payment consent options - payment schedule with only periodUnit`() {
        val paymentConsentOptions = PaymentConsentOptions(
            nextTriggeredBy = PaymentConsent.NextTriggeredBy.MERCHANT,
            merchantTriggerReason = PaymentConsent.MerchantTriggerReason.SCHEDULED,
            termsOfUse = PaymentConsentOptions.TermsOfUse(
                paymentAmountType = PaymentConsentOptions.PaymentAmountType.FIXED,
                fixedPaymentAmount = BigDecimal("100.00"),
                paymentSchedule = PaymentConsentOptions.PaymentSchedule(
                    period = null,
                    periodUnit = PaymentConsentOptions.PeriodUnit.WEEK
                )
            )
        )

        val request = PaymentIntentConfirmRequest.Builder("request-123")
            .setCustomerId("customer-456")
            .setPaymentConsent(paymentConsentOptions)
            .build()

        val paramMap = request.toParamMap()
        val consentMap = paramMap["payment_consent"] as Map<*, *>
        val termsMap = consentMap["terms_of_use"] as Map<*, *>
        val scheduleMap = termsMap["payment_schedule"] as Map<*, *>

        assertNull(scheduleMap["period"])
        assertEquals("WEEK", scheduleMap["period_unit"])
    }

    @Test
    fun `test toParamMap with payment consent options - terms of use with all optional fields`() {
        val paymentConsentOptions = PaymentConsentOptions(
            nextTriggeredBy = PaymentConsent.NextTriggeredBy.MERCHANT,
            merchantTriggerReason = PaymentConsent.MerchantTriggerReason.SCHEDULED,
            termsOfUse = PaymentConsentOptions.TermsOfUse(
                paymentAmountType = PaymentConsentOptions.PaymentAmountType.VARIABLE,
                fixedPaymentAmount = BigDecimal("100.00"),
                maxPaymentAmount = BigDecimal("500.00"),
                minPaymentAmount = BigDecimal("10.00"),
                firstPaymentAmount = BigDecimal("50.00"),
                paymentCurrency = "GBP",
                billingCycleChargeDay = 1,
                startDate = "2024-06-01T00:00:00Z",
                endDate = "2026-06-01T00:00:00Z",
                totalBillingCycles = 24,
                paymentSchedule = PaymentConsentOptions.PaymentSchedule(
                    period = 1,
                    periodUnit = PaymentConsentOptions.PeriodUnit.YEAR
                )
            )
        )

        val request = PaymentIntentConfirmRequest.Builder("request-123")
            .setCustomerId("customer-456")
            .setPaymentConsent(paymentConsentOptions)
            .build()

        val paramMap = request.toParamMap()
        val consentMap = paramMap["payment_consent"] as Map<*, *>
        val termsMap = consentMap["terms_of_use"] as Map<*, *>

        assertEquals("VARIABLE", termsMap["payment_amount_type"])
        assertEquals(BigDecimal("100.00"), termsMap["fixed_payment_amount"])
        assertEquals(BigDecimal("500.00"), termsMap["max_payment_amount"])
        assertEquals(BigDecimal("10.00"), termsMap["min_payment_amount"])
        assertEquals(BigDecimal("50.00"), termsMap["first_payment_amount"])
        assertEquals("GBP", termsMap["payment_currency"])
        assertEquals(1, termsMap["billing_cycle_charge_day"])
        assertEquals("2024-06-01T00:00:00Z", termsMap["start_date"])
        assertEquals("2026-06-01T00:00:00Z", termsMap["end_date"])
        assertEquals(24, termsMap["total_billing_cycles"])
        assertNotNull(termsMap["payment_schedule"])
    }

    @Test
    fun `test PaymentAmountType fromValue with valid values`() {
        assertEquals(
            PaymentConsentOptions.PaymentAmountType.FIXED,
            PaymentConsentOptions.PaymentAmountType.fromValue("FIXED")
        )
        assertEquals(
            PaymentConsentOptions.PaymentAmountType.VARIABLE,
            PaymentConsentOptions.PaymentAmountType.fromValue("VARIABLE")
        )
    }

    @Test
    fun `test PaymentAmountType fromValue with null`() {
        assertNull(PaymentConsentOptions.PaymentAmountType.fromValue(null))
    }

    @Test
    fun `test PaymentAmountType fromValue with invalid value`() {
        assertNull(PaymentConsentOptions.PaymentAmountType.fromValue("INVALID"))
        assertNull(PaymentConsentOptions.PaymentAmountType.fromValue("fixed"))
        assertNull(PaymentConsentOptions.PaymentAmountType.fromValue(""))
    }

    @Test
    fun `test PeriodUnit fromValue with valid values`() {
        assertEquals(
            PaymentConsentOptions.PeriodUnit.DAY,
            PaymentConsentOptions.PeriodUnit.fromValue("DAY")
        )
        assertEquals(
            PaymentConsentOptions.PeriodUnit.WEEK,
            PaymentConsentOptions.PeriodUnit.fromValue("WEEK")
        )
        assertEquals(
            PaymentConsentOptions.PeriodUnit.MONTH,
            PaymentConsentOptions.PeriodUnit.fromValue("MONTH")
        )
        assertEquals(
            PaymentConsentOptions.PeriodUnit.YEAR,
            PaymentConsentOptions.PeriodUnit.fromValue("YEAR")
        )
    }

    @Test
    fun `test PeriodUnit fromValue with null`() {
        assertNull(PaymentConsentOptions.PeriodUnit.fromValue(null))
    }

    @Test
    fun `test PeriodUnit fromValue with invalid value`() {
        assertNull(PaymentConsentOptions.PeriodUnit.fromValue("INVALID"))
        assertNull(PaymentConsentOptions.PeriodUnit.fromValue("day"))
        assertNull(PaymentConsentOptions.PeriodUnit.fromValue("HOUR"))
        assertNull(PaymentConsentOptions.PeriodUnit.fromValue(""))
    }
}
