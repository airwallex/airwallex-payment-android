package com.airwallex.android.core

import android.net.Uri
import android.os.Build
import com.airwallex.android.core.exception.APIException
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.*
import com.airwallex.android.core.model.PaymentConsentFixtures
import com.airwallex.android.core.model.PaymentMethodFixtures
import com.airwallex.android.core.model.parser.AvailablePaymentMethodTypeResponseParser
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class AirwallexPaymentManagerTest {
    private lateinit var paymentManager: PaymentManager
    private val clientSecret = "qadf"
    private val consentId = "cid"
    private val intentId = "iid"
    private val mockMethod = PaymentMethodFixtures.PAYMENT_METHOD
    private val mockConsent = PaymentConsentFixtures.PAYMENTCONSENT
    private val mockIntent: PaymentIntent = mockk()
    private val mockInfo: PaymentMethodTypeInfo = mockk()
    private val mockBank: BankResponse = mockk()
    private lateinit var mockResponse: AvailablePaymentMethodTypeResponse

    @Before
    fun setUp() {
        val testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        mockResponse = AvailablePaymentMethodTypeResponseParser().parse(
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

    @Test
    fun `test start RetrievePaymentConsentOptions operation`() {
        val listener = mockk<Airwallex.PaymentListener<PaymentConsent>>()
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
    fun `test start DisablePaymentConsentOptions operation`() {
        val listener = mockk<Airwallex.PaymentListener<PaymentConsent>>()
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
    fun `test start VerifyPaymentConsentOptions operation`() {
        val listener = mockk<Airwallex.PaymentListener<PaymentConsent>>()
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
    fun `test start CreatePaymentConsentOptions operation`() {
        val listener = mockk<Airwallex.PaymentListener<PaymentConsent>>()
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
    fun `test start RetrieveAvailablePaymentMethodsOptions operation`() {
        val listener = mockk<Airwallex.PaymentListener<AvailablePaymentMethodTypeResponse>>()
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
    fun `test start CreatePaymentMethodOptions operation`() {
        val listener = mockk<Airwallex.PaymentListener<PaymentMethod>>()
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
    fun `test start RetrievePaymentIntentOptions operation`() {
        val listener = mockk<Airwallex.PaymentListener<PaymentIntent>>()
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
    fun `test start ConfirmPaymentIntentOptions operation`() {
        val listener = mockk<Airwallex.PaymentListener<PaymentIntent>>()
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
    fun `test start ContinuePaymentIntentOptions operation`() {
        val listener = mockk<Airwallex.PaymentListener<PaymentIntent>>()
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
    fun `test start RetrievePaymentMethodTypeInfoOptions operation`() {
        val listener = mockk<Airwallex.PaymentListener<PaymentMethodTypeInfo>>()
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
    fun `test start RetrieveBankOptions operation`() {
        val listener = mockk<Airwallex.PaymentListener<BankResponse>>()
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

    @Test
    fun `test start TrackerOptions operation`() {
        mockkObject(AnalyticsLogger)
        val testUrl = "http://abc.com"
        mockkStatic((Uri::class))
        val mockUri = mockk<Uri>()
        val mockBuilder = mockk<Uri.Builder>()
        every { mockUri.toString() } returns testUrl
        every { mockBuilder.build() } returns mockUri
        every { mockBuilder.appendQueryParameter(any(), any()) } returns mockBuilder
        every { Uri.parse(any()).buildUpon() } returns mockBuilder

        val listener = mockk<Airwallex.PaymentListener<Void>>(relaxed = true)
        paymentManager.startOperation(
            Options.TrackerOptions(TrackerRequest()),
            listener
        )
        verify {
            AnalyticsLogger.logError(
                "tracker",
                testUrl,
                any<APIException>()
            )
        }
        verify { listener.onFailed(any()) }

        unmockkAll()
    }
}
