package com.airwallex.android.core.model

import org.json.JSONObject
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class PaymentConsentVerifyRequestTest {

    private val cardRequest = PaymentConsentVerifyRequest.Builder()
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

    private val thirdPartRequest = PaymentConsentVerifyRequest.Builder()
        .setRequestId("aaaa")
        .setReturnUrl("https://www.airwallex.com")
        .setVerificationOptions(
            PaymentConsentVerifyRequest.VerificationOptions(
                type = PaymentMethodType.ALIPAY_CN,
                thirdPartOptions = PaymentConsentVerifyRequest.ThirdPartVerificationOptions(
                    flow = AirwallexPaymentRequestFlow.IN_APP,
                    osType = "android"
                )
            )
        )
        .build()

    @Test
    fun testParams() {
        assertEquals("aaaa", cardRequest.requestId)
        assertEquals("https://www.airwallex.com", cardRequest.returnUrl)
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
            JSONObject(cardRequest.verificationOptions!!.toParamMap()).toString()
        )
    }

    @Test
    fun testToParamsMap() {
        val cardParamMap = cardRequest.toParamMap()
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
            JSONObject(cardParamMap).toString()
        )

        val thirdPartParamMap = thirdPartRequest.toParamMap()
        assertEquals(
            JSONObject(
                mapOf(
                    "request_id" to "aaaa",
                    "return_url" to "https://www.airwallex.com",
                    "verification_options" to mapOf(
                        "alipaycn" to mapOf(
                            "flow" to "inapp",
                            "os_type" to "android"
                        )
                    )
                )
            ).toString(),
            JSONObject(thirdPartParamMap).toString()
        )
    }
}
