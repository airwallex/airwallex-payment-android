package com.airwallex.android.core.model

import org.junit.Test
import kotlin.test.assertEquals

class RetrievePaymentIntentParamsTest {

    private val params by lazy {
        RetrievePaymentIntentParams(
            clientSecret = "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            paymentIntentId = "int_hkdmr7v9rg1j58ky8re"
        )
    }

    @Test
    fun testParams() {
        assertEquals(
            "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            params.clientSecret
        )
        assertEquals("int_hkdmr7v9rg1j58ky8re", params.paymentIntentId)
    }
}
