package com.airwallex.android.core.model

import org.junit.Test
import kotlin.test.assertEquals

class ConfirmPaymentIntentParamsTest {

    private val request = ConfirmPaymentIntentParams.Builder(
        paymentIntentId = "int_hkdmr7v9rg1j58ky8re",
        clientSecret = "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ=="
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
        assertEquals("int_hkdmr7v9rg1j58ky8re", request.paymentIntentId)
        assertEquals(
            "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            request.clientSecret
        )
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
