package com.airwallex.android.model

import kotlin.test.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ConfirmPaymentIntentParamsTest {

    val clientSecret = "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ=="

    val paymentConsentId = "abc"

    @Test
    fun builderConstructor() {
        val confirmPaymentIntentParams = ConfirmPaymentIntentParams.Builder(paymentConsentId, clientSecret)
            .setCVC("123")
            .setCurrency("dollor")
            .setPaymentConsentId("1")
            .build()
        assertEquals(confirmPaymentIntentParams, ConfirmPaymentIntentParamsFixtures.CPIP)
    }
}
