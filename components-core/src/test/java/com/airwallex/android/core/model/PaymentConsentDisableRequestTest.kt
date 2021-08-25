package com.airwallex.android.core.model

import org.junit.Test
import kotlin.test.assertEquals

class PaymentConsentDisableRequestTest {

    private val request = PaymentConsentDisableRequest.Builder()
        .setRequestId("3d702b7b-ac7a-46b5-bf62-9fee0dd713bf")
        .build()

    @Test
    fun testParams() {
        assertEquals("3d702b7b-ac7a-46b5-bf62-9fee0dd713bf", request.requestId)
    }

    @Test
    fun testToParamsMap() {
        val paramMap = request.toParamMap()
        assertEquals(
            mapOf(
                "request_id" to "3d702b7b-ac7a-46b5-bf62-9fee0dd713bf"
            ),
            paramMap
        )
    }
}
