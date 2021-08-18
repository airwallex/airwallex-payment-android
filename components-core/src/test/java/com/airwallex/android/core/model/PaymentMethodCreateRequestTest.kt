package com.airwallex.android.core.model

import org.junit.Test
import kotlin.test.assertEquals

class PaymentMethodCreateRequestTest {

    private val request = PaymentMethodCreateRequest.Builder()
        .setRequestId("aaaa")
        .setCustomerId("111")
        .setType(PaymentMethodType.CARD)
        .setCard(
            PaymentMethod.Card.Builder()
                .setExpiryMonth("12")
                .setExpiryYear("2030")
                .setName("John Doe")
                .setBin("411111")
                .setLast4("1111")
                .setBrand("visa")
                .setIssuerCountryCode("US")
                .setCardType("credit")
                .setFingerprint("7e9cceb282d05675fed72f67e0a4a5ae4e82ff5a96a1b0e55bc45cf63609a055")
                .build()
        )
        .setBilling(null)
        .setMetadata(null)
        .build()

    @Test
    fun testParams() {
        assertEquals("aaaa", request.requestId)
        assertEquals("111", request.customerId)
        assertEquals(PaymentMethodType.CARD, request.type)
        assertEquals(
            PaymentMethod.Card.Builder()
                .setExpiryMonth("12")
                .setExpiryYear("2030")
                .setName("John Doe")
                .setBin("411111")
                .setLast4("1111")
                .setBrand("visa")
                .setIssuerCountryCode("US")
                .setCardType("credit")
                .setFingerprint("7e9cceb282d05675fed72f67e0a4a5ae4e82ff5a96a1b0e55bc45cf63609a055")
                .build().toParamMap(),
            request.card!!.toParamMap()
        )
        assertEquals(null, request.billing)
        assertEquals(null, request.metadata)
    }

    @Test
    fun testToParamsMap() {
        val paramMap = request.toParamMap()
        assertEquals(
            mapOf(
                "request_id" to "aaaa",
                "customer_id" to "111",
                "type" to "card",
                "card" to mapOf(
                    "expiry_month" to "12",
                    "expiry_year" to "2030",
                    "name" to "John Doe",
                    "bin" to "411111",
                    "last4" to "1111",
                    "brand" to "visa",
                    "fingerprint" to "7e9cceb282d05675fed72f67e0a4a5ae4e82ff5a96a1b0e55bc45cf63609a055",
                    "issuer_country_code" to "US",
                    "card_type" to "credit",
                )
            ),
            paramMap
        )
    }
}
