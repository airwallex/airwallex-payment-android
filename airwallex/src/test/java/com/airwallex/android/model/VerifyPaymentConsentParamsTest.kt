package com.airwallex.android.model

import kotlin.test.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.math.BigDecimal

@RunWith(RobolectricTestRunner::class)
class VerifyPaymentConsentParamsTest {

    val clientSecret = "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ=="

    val paymentConsentId = "1"

    val paymentMethodType: PaymentMethodType = PaymentMethodType.CARD

    val returnUrl = "www.airwallex.com"

    @Test
    fun builderConstructor() {
        val verifyPaymentConsentParams = VerifyPaymentConsentParams.Builder(clientSecret, paymentConsentId, paymentMethodType, returnUrl)
            .setCurrency("dollar")
            .setCvc("123")
            .setAmount(BigDecimal.valueOf(0.1))
            .build()
        assertEquals(verifyPaymentConsentParams, VerifyPaymentConsentParamsFixtures.VPCP)
    }
}
