package com.airwallex.android.core.model

import org.junit.Test
import kotlin.test.assertEquals

class DisablePaymentConsentParamsTest {

    private val params = DisablePaymentConsentParams(
        paymentConsentId = "cst_hkdmr7v9rg1j5g4azy6",
        clientSecret = "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2MjkyODExOTQsImV4cCI6MTYyOTI4NDc5NCwiYWNjb3VudF9pZCI6Ijc3Yjg4NjlhLTgyZGMtNGNmYy05YzQ5LTg1YWM1MjAyN2M4YiIsImRhdGFfY2VudGVyX3JlZ2lvbiI6IkhLIiwiY29uc2VudF9pZCI6ImNzdF9oa2Rtcjd2OXJnMWo1ZzRhenk2In0.VHVT12FqDQjeOiRmmOtKVAl3XLiMJWkWDiMzr7QFQHA",
    )

    @Test
    fun testParams() {
        assertEquals(
            "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2MjkyODExOTQsImV4cCI6MTYyOTI4NDc5NCwiYWNjb3VudF9pZCI6Ijc3Yjg4NjlhLTgyZGMtNGNmYy05YzQ5LTg1YWM1MjAyN2M4YiIsImRhdGFfY2VudGVyX3JlZ2lvbiI6IkhLIiwiY29uc2VudF9pZCI6ImNzdF9oa2Rtcjd2OXJnMWo1ZzRhenk2In0.VHVT12FqDQjeOiRmmOtKVAl3XLiMJWkWDiMzr7QFQHA",
            params.clientSecret
        )
        assertEquals("cst_hkdmr7v9rg1j5g4azy6", params.paymentConsentId)
    }
}
