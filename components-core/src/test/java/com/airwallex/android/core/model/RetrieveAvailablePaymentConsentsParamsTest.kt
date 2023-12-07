package com.airwallex.android.core.model

import org.junit.Test
import kotlin.test.assertEquals

class RetrieveAvailablePaymentConsentsParamsTest {
    private val params =
        RetrieveAvailablePaymentConsentsParams.Builder(
            clientSecret = "ap4Uep2dv31m0UKP4",
            customerId = "cid",
            pageNum = 0
        )
            .setNextTriggeredBy(PaymentConsent.NextTriggeredBy.CUSTOMER)
            .setStatus(PaymentConsent.PaymentConsentStatus.VERIFIED)
            .build()

    @Test
    fun testParams() {
        assertEquals(
            "ap4Uep2dv31m0UKP4",
            params.clientSecret
        )
        assertEquals(0, params.pageNum)
        assertEquals(20, params.pageSize)
        assertEquals(PaymentConsent.NextTriggeredBy.CUSTOMER, params.nextTriggeredBy)
        assertEquals(PaymentConsent.PaymentConsentStatus.VERIFIED, params.status)
        assertEquals("cid", params.customerId)
    }
}