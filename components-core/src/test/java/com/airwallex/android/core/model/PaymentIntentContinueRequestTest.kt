package com.airwallex.android.core.model

import org.junit.Test
import kotlin.test.assertEquals

class PaymentIntentContinueRequestTest {

    private val request = PaymentIntentContinueRequest(
        requestId = "aaaa",
        type = PaymentIntentContinueType.DCC,
        useDcc = false
    )

    @Test
    fun testParams() {
        assertEquals("aaaa", request.requestId)
        assertEquals(PaymentIntentContinueType.DCC, request.type)
        assertEquals(null, request.threeDSecure)
        assertEquals(null, request.device)
        assertEquals(false, request.useDcc)
        assertEquals(null, request.device)
    }

    @Test
    fun testToParamsMap() {
        val paramMap = request.toParamMap()
        assertEquals(mapOf("request_id" to "aaaa", "type" to "dcc", "use_dcc" to false), paramMap)
    }
}
