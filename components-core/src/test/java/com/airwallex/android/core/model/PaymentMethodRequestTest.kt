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

    @Test
    fun testGooglePayPaymentMethodRequest() {
        val googlePay = PaymentMethod.GooglePay.Builder()
            .setBilling(
                Billing(
                    firstName = "John",
                    lastName = "Citizen"
                )
            )
            .setPaymentDataType("encrypted_payment_token")
            .setEncryptedPaymentToken("demo_encrypted_payment_token")
            .build()
        val request = PaymentMethodRequest.Builder("googlepay")
            .setGooglePayPaymentMethodRequest(googlePay)
            .build()
        val paramMap = request.toParamMap()
        assertEquals(
            mapOf(
                "type" to "googlepay",
                "googlepay" to mapOf(
                    "billing" to mapOf(
                        "first_name" to "John",
                        "last_name" to "Citizen"
                    ),
                    "payment_data_type" to "encrypted_payment_token",
                    "encrypted_payment_token" to "demo_encrypted_payment_token",
                    "flow" to "inapp",
                    "os_type" to "android"
                )
            ),
            paramMap
        )
    }

    @Test
    fun testCardPaymentMethodRequest() {
        val paramMap = PaymentMethodRequest.Builder("card")
            .setCardPaymentMethodRequest(
                PaymentMethod.Card(cvc = "686"),
                Billing(
                    firstName = "John",
                    lastName = "Citizen"
                )
            )
            .build()
            .toParamMap()

        assertEquals(
            mapOf(
                "type" to "card",
                "card" to mapOf(
                    "billing" to mapOf(
                        "first_name" to "John",
                        "last_name" to "Citizen"
                    ),
                    "cvc" to "686"
                )
            ),
            paramMap
        )
    }
}
