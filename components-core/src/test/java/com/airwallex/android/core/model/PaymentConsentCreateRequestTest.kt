package com.airwallex.android.core.model

import org.junit.Test
import kotlin.test.assertEquals

class PaymentConsentCreateRequestTest {

    private val request = PaymentConsentCreateRequest.Builder()
        .setRequestId("aaaa")
        .setCustomerId("111")
        .setPaymentMethodRequest(
            PaymentMethodRequest.Builder(
                type = PaymentMethodType.ALIPAY_CN
            )
                .setThirdPartyPaymentMethodRequest(
                    name = "aaa",
                    email = "aaa@dd.cc",
                    phone = "123",
                    currency = "CNY",
                    bank = Bank.MAY_BANK
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
        assertEquals("aaaa", request.requestId)
        assertEquals("111", request.customerId)
        assertEquals(
            PaymentMethodRequest.Builder(
                type = PaymentMethodType.ALIPAY_CN
            )
                .setThirdPartyPaymentMethodRequest(
                    name = "aaa",
                    email = "aaa@dd.cc",
                    phone = "123",
                    currency = "CNY",
                    bank = Bank.MAY_BANK
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
                "request_id" to "aaaa",
                "customer_id" to "111",
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
