package com.airwallex.android.core.model

import org.junit.Test
import kotlin.test.assertEquals

class RetrieveAvailablePaymentMethodParamsTest {

    private val params by lazy {
        RetrieveAvailablePaymentMethodParams.Builder(
            clientSecret = "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            pageNum = 0
        )
            .setPageSize(20)
            .setActive(true)
            .setTransactionCurrency("HKD")
            .setTransactionMode(null)
            .build()
    }

    @Test
    fun testParams() {
        assertEquals(
            "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            params.clientSecret
        )
        assertEquals(0, params.pageNum)
        assertEquals(20, params.pageSize)
        assertEquals(true, params.active)
        assertEquals("HKD", params.transactionCurrency)
        assertEquals(null, params.transactionMode)
    }
}
