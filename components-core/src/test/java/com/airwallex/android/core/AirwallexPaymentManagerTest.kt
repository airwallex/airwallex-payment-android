package com.airwallex.android.core

import android.os.Build
import com.airwallex.android.core.model.Options
import com.airwallex.android.core.model.PaymentConsentFixtures
import com.airwallex.android.core.model.PaymentMethodFixtures
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
        val mockMethod = PaymentMethodFixtures.PAYMENT_METHOD
        val mockConsent = PaymentConsentFixtures.PAYMENTCONSENT
        val apiRepository = mockk<ApiRepository>()
        coEvery { apiRepository.retrieveAvailablePaymentMethods(any()) } returns mockResponse
        coEvery { apiRepository.createPaymentMethod(any()) } returns mockMethod
        coEvery { apiRepository.createPaymentConsent(any()) } returns mockConsent
        paymentManager = AirwallexPaymentManager(apiRepository)
    }

    @Test
    fun `test retrieveAvailablePaymentMethods`() = runTest {
        val options = mockk<Options.RetrieveAvailablePaymentMethodsOptions>()
        val response = paymentManager.retrieveAvailablePaymentMethods(options)
        assertEquals(response.items?.first()?.name, "card")
    }

    @Test
    fun `test createPaymentMethod`() = runTest {
        val options = mockk<Options.CreatePaymentMethodOptions>()
        val paymentMethod = paymentManager.createPaymentMethod(options)
        assertEquals(paymentMethod.card?.number, "4012000300001003")
    }

    @Test
    fun `test createPaymentConsent`() = runTest {
        val options = mockk<Options.CreatePaymentConsentOptions>()
        val paymentConsent = paymentManager.createPaymentConsent(options)
        assertEquals(paymentConsent.paymentMethod?.card?.name, "Adam")
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
