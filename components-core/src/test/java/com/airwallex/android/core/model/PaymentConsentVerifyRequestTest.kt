package com.airwallex.android.core.model

import org.json.JSONObject
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class PaymentConsentVerifyRequestTest {

    private val request = PaymentConsentVerifyRequest.Builder()
        .setRequestId("aaaa")
        .setReturnUrl("https://www.airwallex.com")
        .setVerificationOptions(
            PaymentConsentVerifyRequest.VerificationOptions(
                type = PaymentMethodType.CARD,
                cardOptions = PaymentConsentVerifyRequest.CardVerificationOptions(
                    amount = BigDecimal.valueOf(1),
                    currency = "HKD",
                    cvc = "123"
                )
            )
        )
        .build()

    @Test
    fun testParams() {
        assertEquals("aaaa", request.requestId)
        assertEquals("https://www.airwallex.com", request.returnUrl)
        assertEquals(
            JSONObject(
                mapOf(
                    "card" to mapOf(
                        "amount" to 1,
                        "currency" to "HKD",
                        "cvc" to "123"
                    )
                )
            ).toString(),
            JSONObject(request.verificationOptions!!.toParamMap()).toString()
        )
    }

    @Test
    fun testToParamsMap() {
        val paramMap = request.toParamMap()
        assertEquals(
            JSONObject(
                mapOf(
                    "request_id" to "aaaa",
                    "return_url" to "https://www.airwallex.com",
                    "verification_options" to mapOf(
                        "card" to mapOf(
                            "amount" to 1,
                            "currency" to "HKD",
                            "cvc" to "123"
                        )
                    )
                )
            ).toString(),
            JSONObject(paramMap).toString()
        )
    }
}
