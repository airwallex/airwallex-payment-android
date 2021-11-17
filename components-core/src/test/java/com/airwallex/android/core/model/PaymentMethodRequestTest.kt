package com.airwallex.android.core.model

import org.junit.Test
import kotlin.test.assertEquals

class PaymentMethodRequestTest {

    private val request = PaymentMethodRequest.Builder(
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
        .build()

    @Test
    fun testParams() {
        assertEquals("alipaycn", request.type)
        assertEquals(null, request.card)
    }

    @Test
    fun testToParamsMap() {
        val paramMap = request.toParamMap()
        assertEquals(
            mapOf(
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
            paramMap
        )
    }
}
