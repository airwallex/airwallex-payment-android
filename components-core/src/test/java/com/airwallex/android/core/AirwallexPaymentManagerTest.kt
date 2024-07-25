package com.airwallex.android.core

import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.BankResponse
import com.airwallex.android.core.model.Options
import com.airwallex.android.core.model.Page
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentConsentCreateRequest
import com.airwallex.android.core.model.PaymentConsentDisableRequest
import com.airwallex.android.core.model.PaymentConsentFixtures
import com.airwallex.android.core.model.PaymentConsentVerifyRequest
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PaymentIntentConfirmRequest
import com.airwallex.android.core.model.PaymentIntentContinueRequest
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodCreateRequest
import com.airwallex.android.core.model.PaymentMethodFixtures
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.core.model.PaymentMethodTypeInfo
import com.airwallex.android.core.model.parser.AvailablePaymentMethodTypeParser
import com.airwallex.android.core.model.parser.PageParser
import com.airwallex.android.core.model.parser.PaymentConsentParser
import com.airwallex.android.core.util.BuildHelper
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class AirwallexPaymentManagerTest {
    private lateinit var dispatcher: TestDispatcher

    private lateinit var paymentManager: PaymentManager
    private val clientSecret = "qadf"
    private val consentId = "cid"
    private val intentId = "iid"
    private val mockMethod = PaymentMethodFixtures.PAYMENT_METHOD
    private val mockConsent = PaymentConsentFixtures.PAYMENTCONSENT
    private val mockIntent: PaymentIntent = mockk()
    private val mockInfo: PaymentMethodTypeInfo = mockk()
    private val mockBank: BankResponse = mockk()
    private lateinit var mockResponse: Page<AvailablePaymentMethodType>
    private lateinit var mockConsents: Page<PaymentConsent>

    @Before
    fun setUp() {
        mockkObject(BuildHelper)
        dispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(dispatcher)

        mockResponse = PageParser(AvailablePaymentMethodTypeParser()).parse(
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

        mockConsents = PageParser(PaymentConsentParser()).parse(
            JSONObject(
                """
        {
            "items":[
                {
                  "payment_method": {
                    "type": "card",
                    "card": {
                        "name": "John",
                        "number_type": "PAN"
                    }
                  },
                  "next_triggered_by": "customer",
                  "status": "VERIFIED"
                }   
            ],
            "has_more":false
        }
                """.trimIndent()
            )
        )

        val apiRepository = mockk<ApiRepository>()
        coEvery { apiRepository.retrieveAvailablePaymentMethods(any()) } returns mockResponse
        coEvery { apiRepository.retrieveAvailablePaymentConsents(any()) } returns mockConsents
        coEvery { apiRepository.createPaymentMethod(any()) } returns mockMethod
        coEvery { apiRepository.createPaymentConsent(any()) } returns mockConsent
        coEvery { apiRepository.retrievePaymentConsent(any()) } returns mockConsent
        coEvery { apiRepository.disablePaymentConsent(any()) } returns mockConsent
        coEvery { apiRepository.verifyPaymentConsent(any()) } returns mockConsent
        coEvery { apiRepository.retrievePaymentIntent(any()) } returns mockIntent
        coEvery { apiRepository.confirmPaymentIntent(any()) } returns mockIntent
        coEvery { apiRepository.continuePaymentIntent(any()) } returns mockIntent
        coEvery { apiRepository.retrievePaymentMethodTypeInfo(any()) } returns mockInfo
        coEvery { apiRepository.retrieveBanks(any()) } returns mockBank
        paymentManager = AirwallexPaymentManager(apiRepository)
    }

    @After
    fun unmockk() {
        unmockkAll()
    }

    @Test
    fun `test retrieveAvailablePaymentMethods`() = runTest {
        val options = mockk<Options.RetrieveAvailablePaymentMethodsOptions>()
        val response = paymentManager.retrieveAvailablePaymentMethods(options)
        assertEquals(response.items.first().name, "card")
    }

    @Test
    fun `test retrieveAvailablePaymentConsents`() = runTest {
        val options = mockk<Options.RetrieveAvailablePaymentConsentsOptions>()
        val response = paymentManager.retrieveAvailablePaymentConsents(options)
        assertEquals(response.items.first().paymentMethod?.card?.name, "John")
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
        every { BuildHelper.manufacturer } returns "huawei"
        every { BuildHelper.model } returns "mate30 pro"
        every { BuildHelper.versionRelease } returns "10.0"
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

    @Test
    fun `test start RetrievePaymentConsentOptions operation`() = runTest {
        val listener = mockk<Airwallex.PaymentListener<PaymentConsent>>(relaxed = true)
        paymentManager.startOperation(
            Options.RetrievePaymentConsentOptions(
                clientSecret,
                consentId
            ),
            listener
        )
        verify { listener.onSuccess(mockConsent) }
    }

    @Test
    fun `test start DisablePaymentConsentOptions operation`() = runTest {
        val listener = mockk<Airwallex.PaymentListener<PaymentConsent>>(relaxed = true)
        paymentManager.startOperation(
            Options.DisablePaymentConsentOptions(
                clientSecret,
                consentId,
                PaymentConsentDisableRequest()
            ),
            listener
        )
        verify { listener.onSuccess(mockConsent) }
    }

    @Test
    fun `test start VerifyPaymentConsentOptions operation`() = runTest {
        val listener = mockk<Airwallex.PaymentListener<PaymentConsent>>(relaxed = true)
        paymentManager.startOperation(
            Options.VerifyPaymentConsentOptions(
                clientSecret,
                consentId,
                PaymentConsentVerifyRequest()
            ),
            listener
        )
        verify { listener.onSuccess(mockConsent) }
    }

    @Test
    fun `test start CreatePaymentConsentOptions operation`() = runTest {
        val listener = mockk<Airwallex.PaymentListener<PaymentConsent>>(relaxed = true)
        paymentManager.startOperation(
            Options.CreatePaymentConsentOptions(
                clientSecret,
                PaymentConsentCreateRequest()
            ),
            listener
        )
        verify { listener.onSuccess(mockConsent) }
    }

    @Test
    fun `test start RetrieveAvailablePaymentMethodsOptions operation`() = runTest {
        val listener = mockk<Airwallex.PaymentListener<Page<AvailablePaymentMethodType>>>(relaxed = true)
        paymentManager.startOperation(
            Options.RetrieveAvailablePaymentMethodsOptions(
                clientSecret,
                0,
                5,
                null,
                null,
                null,
                null
            ),
            listener
        )
        verify { listener.onSuccess(mockResponse) }
    }

    @Test
    fun `test start CreatePaymentMethodOptions operation`() = runTest {
        val listener = mockk<Airwallex.PaymentListener<PaymentMethod>>(relaxed = true)
        paymentManager.startOperation(
            Options.CreatePaymentMethodOptions(
                clientSecret,
                PaymentMethodCreateRequest()
            ),
            listener
        )
        verify { listener.onSuccess(mockMethod) }
    }

    @Test
    fun `test start RetrievePaymentIntentOptions operation`() = runTest {
        val listener = mockk<Airwallex.PaymentListener<PaymentIntent>>(relaxed = true)
        paymentManager.startOperation(
            Options.RetrievePaymentIntentOptions(
                clientSecret,
                intentId
            ),
            listener
        )
        verify { listener.onSuccess(mockIntent) }
    }

    @Test
    fun `test start ConfirmPaymentIntentOptions operation`() = runTest {
        val listener = mockk<Airwallex.PaymentListener<PaymentIntent>>(relaxed = true)
        paymentManager.startOperation(
            Options.ConfirmPaymentIntentOptions(
                clientSecret,
                intentId,
                PaymentIntentConfirmRequest()
            ),
            listener
        )
        verify { listener.onSuccess(mockIntent) }
    }

    @Test
    fun `test start ContinuePaymentIntentOptions operation`() = runTest {
        val listener = mockk<Airwallex.PaymentListener<PaymentIntent>>(relaxed = true)
        paymentManager.startOperation(
            Options.ContinuePaymentIntentOptions(
                clientSecret,
                intentId,
                PaymentIntentContinueRequest()
            ),
            listener
        )
        verify { listener.onSuccess(mockIntent) }
    }

    @Test
    fun `test start RetrievePaymentMethodTypeInfoOptions operation`() = runTest {
        val listener = mockk<Airwallex.PaymentListener<PaymentMethodTypeInfo>>(relaxed = true)
        paymentManager.startOperation(
            Options.RetrievePaymentMethodTypeInfoOptions(
                clientSecret,
                PaymentMethodType.CARD.value,
                null,
                null,
                null,
                null
            ),
            listener
        )
        verify { listener.onSuccess(mockInfo) }
    }

    @Test
    fun `test start RetrieveBankOptions operation`() = runTest {
        val listener = mockk<Airwallex.PaymentListener<BankResponse>>(relaxed = true)
        paymentManager.startOperation(
            Options.RetrieveBankOptions(
                clientSecret,
                PaymentMethodType.CARD.value,
                null,
                null,
                null,
                null
            ),
            listener
        )
        verify { listener.onSuccess(mockBank) }
    }
}
