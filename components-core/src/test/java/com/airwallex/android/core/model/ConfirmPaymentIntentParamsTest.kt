package com.airwallex.android.core.model

import org.junit.Test
import kotlin.test.assertEquals

class ConfirmPaymentIntentParamsTest {

    private val request = ConfirmPaymentIntentParams.Builder(
        paymentIntentId = "123",
        clientSecret = "abc"
    )
        .setCustomerId("111")
        .setCVC("123")
        .setPPROAdditionalInfo(null)
        .setPaymentConsentId("222")
        .setCurrency("CNY")
        .setReturnUrl("http://www.airwallex.com")
        .setPaymentMethod(PaymentMethodType.WECHAT)
        .build()

    @Test
    fun testParams() {
        assertEquals("123", request.paymentIntentId)
        assertEquals("abc", request.clientSecret)
        assertEquals("111", request.customerId)
        assertEquals(PaymentMethodType.WECHAT, request.paymentMethodType)
        assertEquals(null, request.paymentMethod)
        assertEquals("123", request.cvc)
        assertEquals("222", request.paymentConsentId)
        assertEquals("CNY", request.currency)
        assertEquals(null, request.pproAdditionalInfo)
        assertEquals("http://www.airwallex.com", request.returnUrl)
    }
}
