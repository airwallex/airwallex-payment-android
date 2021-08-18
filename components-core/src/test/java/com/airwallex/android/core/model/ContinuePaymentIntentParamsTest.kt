package com.airwallex.android.core.model

import org.junit.Test
import kotlin.test.assertEquals

class ContinuePaymentIntentParamsTest {

    private val request = ContinuePaymentIntentParams(
        paymentIntentId = "int_hkdmr7v9rg1j58ky8re",
        clientSecret = "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
        type = PaymentIntentContinueType.ENROLLMENT,
        device = Device(
            deviceId = "airwallex_cybsint_hkdmr7v9rg1j58ky8re1629281144897",
            deviceModel = "mobile",
            sdkVersion = "2.0.6",
            platformType = "Android",
            deviceOS = "10"
        ),
        threeDSecure = ThreeDSecure(
            deviceDataCollectionRes = "1_3129dc55-e19c-4eb9-8be6-d37d1686a129"
        ),
        useDcc = false
    )

    @Test
    fun testParams() {
        assertEquals("int_hkdmr7v9rg1j58ky8re", request.paymentIntentId)
        assertEquals(
            "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            request.clientSecret
        )
        assertEquals(PaymentIntentContinueType.ENROLLMENT, request.type)
        assertEquals(
            Device(
                deviceId = "airwallex_cybsint_hkdmr7v9rg1j58ky8re1629281144897",
                deviceModel = "mobile",
                sdkVersion = "2.0.6",
                platformType = "Android",
                deviceOS = "10"
            ).toString(),
            request.device.toString()
        )
        assertEquals(
            ThreeDSecure(
                deviceDataCollectionRes = "1_3129dc55-e19c-4eb9-8be6-d37d1686a129"
            ).toString(),
            request.threeDSecure.toString()
        )
        assertEquals(false, request.useDcc)
    }
}
