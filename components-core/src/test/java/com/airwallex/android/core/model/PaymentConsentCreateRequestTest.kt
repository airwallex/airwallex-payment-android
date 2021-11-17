package com.airwallex.android.core.model

import org.junit.Test
import kotlin.test.assertEquals

class PaymentConsentCreateRequestTest {

    private val request = PaymentConsentCreateRequest.Builder()
        .setRequestId("3d702b7b-ac7a-46b5-bf62-9fee0dd713bf")
        .setCustomerId("cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu")
        .setPaymentMethodRequest(
            PaymentMethodRequest.Builder(
                type = "alipaycn"
            )
                .setThirdPartyPaymentMethodRequest(
                    mapOf(
                        "bank_name" to "maybank",
                        "country_code" to "CN",
                        "shopper_name" to "aaa",
                        "shopper_email" to "aaa@dd.cc",
                        "shopper_phone" to "123",
                        "flow" to "inapp",
                        "os_type" to "android"
                    )
                )
                .build()
        )
        .setNextTriggeredBy(PaymentConsent.NextTriggeredBy.CUSTOMER)
        .setRequiresCvc(false)
        .setMetadata(null)
        .setMerchantTriggerReason(PaymentConsent.MerchantTriggerReason.UNSCHEDULED)
        .build()

    @Test
    fun testParams() {
        assertEquals("3d702b7b-ac7a-46b5-bf62-9fee0dd713bf", request.requestId)
        assertEquals("cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu", request.customerId)
        assertEquals(
            PaymentMethodRequest.Builder(
                type = "alipaycn"
            ).setThirdPartyPaymentMethodRequest(
                mapOf(
                    "bank_name" to "maybank",
                    "country_code" to "CN",
                    "shopper_name" to "aaa",
                    "shopper_email" to "aaa@dd.cc",
                    "shopper_phone" to "123",
                    "flow" to "inapp",
                    "os_type" to "android"
                )
            )
                .build().toParamMap(),
            request.paymentMethodRequest!!.toParamMap()
        )
        assertEquals(PaymentConsent.NextTriggeredBy.CUSTOMER, request.nextTriggeredBy)
        assertEquals(
            PaymentConsent.MerchantTriggerReason.UNSCHEDULED,
            request.merchantTriggerReason
        )
        assertEquals(false, request.requiresCvc)
        assertEquals(null, request.metadata)
        assertEquals(
            PaymentConsent.MerchantTriggerReason.UNSCHEDULED,
            request.merchantTriggerReason
        )
    }

    @Test
    fun testToParamsMap() {
        val paramMap = request.toParamMap()
        assertEquals(
            mapOf(
                "request_id" to "3d702b7b-ac7a-46b5-bf62-9fee0dd713bf",
                "customer_id" to "cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu",
                "payment_method" to mapOf(
                    "type" to "alipaycn",
                    "alipaycn" to mapOf(
                        "bank_name" to "maybank",
                        "country_code" to "CN",
                        "shopper_name" to "aaa",
                        "shopper_email" to "aaa@dd.cc",
                        "shopper_phone" to "123",
                        "flow" to "inapp",
                        "os_type" to "android"
                    )
                ),
                "next_triggered_by" to "customer",
                "merchant_trigger_reason" to "unscheduled",
                "requires_cvc" to false
            ),
            paramMap
        )
    }
}
