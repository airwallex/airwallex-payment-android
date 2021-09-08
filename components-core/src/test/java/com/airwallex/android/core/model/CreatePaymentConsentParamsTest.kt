package com.airwallex.android.core.model

import org.junit.Test
import kotlin.test.assertEquals

class CreatePaymentConsentParamsTest {

    private val params = CreatePaymentConsentParams.Builder(
        clientSecret = "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2MjkyODExOTQsImV4cCI6MTYyOTI4NDc5NCwiYWNjb3VudF9pZCI6Ijc3Yjg4NjlhLTgyZGMtNGNmYy05YzQ5LTg1YWM1MjAyN2M4YiIsImRhdGFfY2VudGVyX3JlZ2lvbiI6IkhLIiwiY29uc2VudF9pZCI6ImNzdF9oa2Rtcjd2OXJnMWo1ZzRhenk2In0.VHVT12FqDQjeOiRmmOtKVAl3XLiMJWkWDiMzr7QFQHA",
        paymentMethodType = PaymentMethodType.CARD,
        customerId = "cus_hkdmnb922g1j36140vv",
        nextTriggeredBy = PaymentConsent.NextTriggeredBy.CUSTOMER
    )
        .setPaymentMethodId("mtd_hkdmtjbfmg1j5g3kdo1")
        .setMerchantTriggerReason(PaymentConsent.MerchantTriggerReason.UNSCHEDULED)
        .setRequiresCvc(false)
        .build()

    @Test
    fun testParams() {
        assertEquals(
            "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2MjkyODExOTQsImV4cCI6MTYyOTI4NDc5NCwiYWNjb3VudF9pZCI6Ijc3Yjg4NjlhLTgyZGMtNGNmYy05YzQ5LTg1YWM1MjAyN2M4YiIsImRhdGFfY2VudGVyX3JlZ2lvbiI6IkhLIiwiY29uc2VudF9pZCI6ImNzdF9oa2Rtcjd2OXJnMWo1ZzRhenk2In0.VHVT12FqDQjeOiRmmOtKVAl3XLiMJWkWDiMzr7QFQHA",
            params.clientSecret
        )
        assertEquals(PaymentMethodType.CARD, params.paymentMethodType)
        assertEquals("cus_hkdmnb922g1j36140vv", params.customerId)
        assertEquals("mtd_hkdmtjbfmg1j5g3kdo1", params.paymentMethodId)
        assertEquals(PaymentConsent.MerchantTriggerReason.UNSCHEDULED, params.merchantTriggerReason)
        assertEquals(false, params.requiresCvc)
    }
}
