package com.airwallex.android.core.model

import org.junit.Test
import kotlin.test.assertEquals

class PaymentMethodRequestTest {

    private val request = PaymentMethodRequest.Builder(
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

    @Test
    fun testParams() {
        assertEquals(PaymentMethodType.ALIPAY_CN, request.type)
        assertEquals(
            AirwallexPaymentRequest(
                name = "aaa",
                email = "aaa@dd.cc",
                phone = "123",
                countryCode = "CN",
                bank = Bank.MAY_BANK
            ),
            request.redirectRequest
        )
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
