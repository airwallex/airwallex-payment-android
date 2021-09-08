package com.airwallex.android.core.model

import org.junit.Test
import kotlin.test.assertEquals

class CreatePaymentMethodParamsTest {

    private val params = CreatePaymentMethodParams(
        customerId = "cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu",
        clientSecret = "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2MjkyODExOTQsImV4cCI6MTYyOTI4NDc5NCwiYWNjb3VudF9pZCI6Ijc3Yjg4NjlhLTgyZGMtNGNmYy05YzQ5LTg1YWM1MjAyN2M4YiIsImRhdGFfY2VudGVyX3JlZ2lvbiI6IkhLIiwiY29uc2VudF9pZCI6ImNzdF9oa2Rtcjd2OXJnMWo1ZzRhenk2In0.VHVT12FqDQjeOiRmmOtKVAl3XLiMJWkWDiMzr7QFQHA",
        card = PaymentMethod.Card(),
        billing = null
    )

    @Test
    fun testParams() {
        assertEquals(
            "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2MjkyODExOTQsImV4cCI6MTYyOTI4NDc5NCwiYWNjb3VudF9pZCI6Ijc3Yjg4NjlhLTgyZGMtNGNmYy05YzQ5LTg1YWM1MjAyN2M4YiIsImRhdGFfY2VudGVyX3JlZ2lvbiI6IkhLIiwiY29uc2VudF9pZCI6ImNzdF9oa2Rtcjd2OXJnMWo1ZzRhenk2In0.VHVT12FqDQjeOiRmmOtKVAl3XLiMJWkWDiMzr7QFQHA",
            params.clientSecret
        )
        assertEquals("cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu", params.customerId)
        assertEquals(PaymentMethod.Card(), params.card)
        assertEquals(null, params.billing)
    }
}
