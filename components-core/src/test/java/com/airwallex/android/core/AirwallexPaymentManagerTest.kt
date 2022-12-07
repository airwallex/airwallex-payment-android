package com.airwallex.android.core

import android.os.Build
import com.airwallex.android.core.model.parser.AvailablePaymentMethodTypeResponseParser
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class AirwallexPaymentManagerTest {
    private lateinit var paymentManager: PaymentManager

    @Before
    fun setUp() {
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
                   "flows":["inapp"]
                  }   
            ],
            "has_more":false
        }
                """.trimIndent()
            )
        )
        val apiRepository = mockk<ApiRepository>()
        coEvery { apiRepository.retrieveAvailablePaymentMethods(any()) } returns mockResponse
        paymentManager = AirwallexPaymentManager(apiRepository)
    }

    @Test
    fun `test start RetrieveAvailablePaymentMethods operation`() = runTest {
        val options = mockk<AirwallexApiRepository.RetrieveAvailablePaymentMethodsOptions>()
        val response = paymentManager.startRetrieveAvailablePaymentMethodsOperation(options)
        assertEquals(response.items?.first()?.name, "card")
    }

    @Test
    fun `test buildDeviceInfo`() {
        mockkStatic(Build::class)
        Build::class.java.getField("MANUFACTURER").setFinalStatic("huawei")
        Build::class.java.getField("MODEL").setFinalStatic("mate30 pro")
        Build.VERSION::class.java.getField("RELEASE").setFinalStatic("10.0")
        assertEquals(
            mapOf(
                "device_id" to "123456",
                "mobile" to mapOf(
                    "device_model" to "Huawei Mate30 pro",
                    "os_type" to "Android",
                    "os_version" to "10.0"
                )
            ),
            paymentManager.buildDeviceInfo("123456").toParamMap()
        )
    }
}
