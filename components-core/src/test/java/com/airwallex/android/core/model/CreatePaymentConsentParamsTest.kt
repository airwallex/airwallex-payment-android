package com.airwallex.android.core.model

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CreatePaymentConsentParamsTest {

    private val googlePay = PaymentMethod.GooglePay.Builder()
        .setBilling(null)
        .setPaymentDataType("encrypted_payment_token")
        .setEncryptedPaymentToken("demo_encrypted_payment_token")
        .build()

    private val paramsBuilder = CreatePaymentConsentParams.Builder(
        clientSecret = "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2MjkyODExOTQsImV4cCI6MTYyOTI4NDc5NCwiYWNjb3VudF9pZCI6Ijc3Yjg4NjlhLTgyZGMtNGNmYy05YzQ5LTg1YWM1MjAyN2M4YiIsImRhdGFfY2VudGVyX3JlZ2lvbiI6IkhLIiwiY29uc2VudF9pZCI6ImNzdF9oa2Rtcjd2OXJnMWo1ZzRhenk2In0.VHVT12FqDQjeOiRmmOtKVAl3XLiMJWkWDiMzr7QFQHA",
        paymentMethodType = "card",
        customerId = "cus_hkdmnb922g1j36140vv",
        nextTriggeredBy = PaymentConsent.NextTriggeredBy.CUSTOMER
    )
        .setPaymentMethodId("mtd_hkdmtjbfmg1j5g3kdo1")
        .setMerchantTriggerReason(PaymentConsent.MerchantTriggerReason.UNSCHEDULED)
        .setGooglePay(googlePay)
        .setRequiresCvc(false)

    @Test
    fun testParams() {
        val params = paramsBuilder.build()
        assertEquals(
            "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2MjkyODExOTQsImV4cCI6MTYyOTI4NDc5NCwiYWNjb3VudF9pZCI6Ijc3Yjg4NjlhLTgyZGMtNGNmYy05YzQ5LTg1YWM1MjAyN2M4YiIsImRhdGFfY2VudGVyX3JlZ2lvbiI6IkhLIiwiY29uc2VudF9pZCI6ImNzdF9oa2Rtcjd2OXJnMWo1ZzRhenk2In0.VHVT12FqDQjeOiRmmOtKVAl3XLiMJWkWDiMzr7QFQHA",
            params.clientSecret
        )
        assertEquals("card", params.paymentMethodType)
        assertEquals("cus_hkdmnb922g1j36140vv", params.customerId)
        assertEquals("mtd_hkdmtjbfmg1j5g3kdo1", params.paymentMethodId)
        assertEquals(PaymentConsent.MerchantTriggerReason.UNSCHEDULED, params.merchantTriggerReason)
        assertEquals("encrypted_payment_token", params.googlePay?.paymentDataType)
        assertEquals("demo_encrypted_payment_token", params.googlePay?.encryptedPaymentToken)
        assertEquals(false, params.requiresCvc)
    }

    @Test
    fun testCreateCardParamsWhenMerchantTriggerReasonIsNull() {
        val params = CreatePaymentConsentParams.createCardParams(
            clientSecret = "asdfqrfqrfag",
            customerId = "cus_hkdmnb922g1j36140vv",
            paymentMethodId = "abcd",
            nextTriggeredBy = PaymentConsent.NextTriggeredBy.MERCHANT,
            merchantTriggerReason = null,
            requiresCvc = true
        )
        assertEquals(params.clientSecret, "asdfqrfqrfag")
        assertEquals(params.customerId, "cus_hkdmnb922g1j36140vv")
        assertEquals(params.paymentMethodId, "abcd")
        assertEquals(params.nextTriggeredBy, PaymentConsent.NextTriggeredBy.MERCHANT)
        assertNull(params.merchantTriggerReason)
        assertEquals(params.requiresCvc, true)
    }

    @Test
    fun testSetMerchantTriggerReasonWhenNull() {
        val params = paramsBuilder
            .setMerchantTriggerReason(null)
            .build()
        assertNull(params.merchantTriggerReason)
    }

}
