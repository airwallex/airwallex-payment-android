package com.airwallex.android.core.model

import org.junit.Test
import kotlin.test.assertEquals

class PaymentConsentVerifyRequestTest {

    private val request = PaymentConsentVerifyRequest.Builder()
        .setRequestId("aaaa")
        .setReturnUrl("https://www.airwallex.com")
        .setVerificationOptions(null)
        .build()

    @Test
    fun testParams() {
        assertEquals("aaaa", request.requestId)
        assertEquals("https://www.airwallex.com", request.returnUrl)
        assertEquals(null, request.verificationOptions)
    }

    @Test
    fun testToParamsMap() {
        val paramMap = request.toParamMap()
        assertEquals(
            mapOf(
                "request_id" to "aaaa",
                "return_url" to "https://www.airwallex.com"
            ),
            paramMap
        )
    }
}
