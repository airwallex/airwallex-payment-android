package com.airwallex.android.model

import kotlin.test.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CreatePaymentConsentParamsTest {

    val clientSecret = "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ=="

    val customerId = "1"

    val paymentMethodType: PaymentMethodType = PaymentMethodType.CARD

    val nextTriggeredBy = PaymentConsent.NextTriggeredBy.CUSTOMER

    @Test
    fun builderConstructor() {
        val createPaymentConsentParams = CreatePaymentConsentParams.Builder(clientSecret, customerId, paymentMethodType, nextTriggeredBy)
            .setPaymentMethodId("1")
            .setRequiresCvc(true)
            .setMerchantTriggerReason(PaymentConsent.MerchantTriggerReason.UNSCHEDULED)
            .build()
        assertEquals(createPaymentConsentParams, CreatePaymentConsentParamsFixtures.createPaymentConsentParams)
    }
}
