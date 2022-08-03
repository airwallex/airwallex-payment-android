package com.airwallex.android.core

import com.airwallex.android.core.model.AvailablePaymentMethodTypeResponse
import com.airwallex.android.core.model.parser.AvailablePaymentMethodTypeResponseParser
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.junit.Test
import kotlin.test.assertEquals

class AirwallexPaymentManagerTest {
    @Test
    fun `test suspend startOperation when option is RetrieveAvailablePaymentMethods`() = runTest {
        val mockResponse = AvailablePaymentMethodTypeResponseParser().parse(
            JSONObject(
                """
        {
        "items":[ 
                  {
                   "name":"card",
                   "transaction_mode":"oneoff",
                   "active":true,
                   "transaction_currencies":["dollar","RMB"],
                   "flows":["IN_APP"]
                  }   
            ],
            "has_more":false
        }
                """.trimIndent()
            )
        )
        val apiRepository = mockk<ApiRepository>()
        val options = mockk<AirwallexApiRepository.RetrieveAvailablePaymentMethodsOptions>()
        coEvery { apiRepository.retrieveAvailablePaymentMethods(any()) } returns mockResponse
        val paymentManager = AirwallexPaymentManager(apiRepository)
        val response = paymentManager.startOperation<AvailablePaymentMethodTypeResponse>(options)
        assertEquals(response?.items?.first()?.name, "card")
    }
}
