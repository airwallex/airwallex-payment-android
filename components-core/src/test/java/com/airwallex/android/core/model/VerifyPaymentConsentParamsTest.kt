package com.airwallex.android.core.model

import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class VerifyPaymentConsentParamsTest {

    private val params = VerifyPaymentConsentParams.Builder(
        clientSecret = "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2MjkyODExOTQsImV4cCI6MTYyOTI4NDc5NCwiYWNjb3VudF9pZCI6Ijc3Yjg4NjlhLTgyZGMtNGNmYy05YzQ5LTg1YWM1MjAyN2M4YiIsImRhdGFfY2VudGVyX3JlZ2lvbiI6IkhLIiwiY29uc2VudF9pZCI6ImNzdF9oa2Rtcjd2OXJnMWo1ZzRhenk2In0.VHVT12FqDQjeOiRmmOtKVAl3XLiMJWkWDiMzr7QFQHA",
        paymentConsentId = "cst_hkdmr7v9rg1j5g4azy6",
        paymentMethodType = "card"
    )
        .setAmount(BigDecimal.valueOf(1))
        .setCurrency("HKD")
        .setCvc("123")
        .setReturnUrl("airwallexcheckou://com.airwallex.paymentacceptance")
        .build()

    @Test
    fun testParams() {
        assertEquals(
            "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2MjkyODExOTQsImV4cCI6MTYyOTI4NDc5NCwiYWNjb3VudF9pZCI6Ijc3Yjg4NjlhLTgyZGMtNGNmYy05YzQ5LTg1YWM1MjAyN2M4YiIsImRhdGFfY2VudGVyX3JlZ2lvbiI6IkhLIiwiY29uc2VudF9pZCI6ImNzdF9oa2Rtcjd2OXJnMWo1ZzRhenk2In0.VHVT12FqDQjeOiRmmOtKVAl3XLiMJWkWDiMzr7QFQHA",
            params.clientSecret
        )
        assertEquals("cst_hkdmr7v9rg1j5g4azy6", params.paymentConsentId)
        assertEquals("card", params.paymentMethodType)
        assertEquals(BigDecimal.valueOf(1), params.amount)
        assertEquals("HKD", params.currency)
        assertEquals("airwallexcheckou://com.airwallex.paymentacceptance", params.returnUrl)
        assertEquals("123", params.cvc)
    }
}
