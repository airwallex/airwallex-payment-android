package com.airwallex.android.core

import android.net.Uri
import com.airwallex.android.core.exception.APIConnectionException
import com.airwallex.android.core.exception.AuthenticationException
import com.airwallex.android.core.exception.InvalidRequestException
import com.airwallex.android.core.exception.PermissionException
import com.airwallex.android.core.http.AirwallexHttpClient
import com.airwallex.android.core.http.AirwallexHttpRequest
import com.airwallex.android.core.http.AirwallexHttpResponse
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.model.AirwallexPaymentRequestFlow
import com.airwallex.android.core.model.Options
import com.airwallex.android.core.model.Options.RetrieveAvailablePaymentMethodsOptions
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentConsentCreateRequest
import com.airwallex.android.core.model.PaymentConsentDisableRequest
import com.airwallex.android.core.model.PaymentConsentVerifyRequest
import com.airwallex.android.core.model.PaymentIntentConfirmRequest
import com.airwallex.android.core.model.PaymentIntentContinueRequest
import com.airwallex.android.core.model.PaymentMethodCreateRequest
import com.airwallex.android.core.model.TransactionMode
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import java.io.IOException
import java.net.HttpURLConnection
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

@Suppress("LargeClass")
class AirwallexApiRepositoryTest {

    private lateinit var apiRepository: AirwallexApiRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        mockkConstructor(AirwallexHttpClient::class)
        val httpClientMock = mockk<AirwallexHttpClient>(relaxed = true)
        every { anyConstructed<AirwallexHttpClient>().execute(any()) } answers {
            httpClientMock.execute(firstArg())
        }
        mockkObject(AirwallexLogger)
        apiRepository = AirwallexApiRepository()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun executeMockWeChatLogsDebugMessageOnFailure() = runBlocking<Unit> {
        coEvery { anyConstructed<AirwallexHttpClient>().execute(any<AirwallexHttpRequest>()) } throws IOException(
            "Mocked IO Exception"
        )
        apiRepository.executeMockWeChat("https://mock.wechat.url")
        verify { AirwallexLogger.debug("Execute Mock WeChat failed.") }
    }

    @Test
    fun retrieveAvailablePaymentMethodsOptionsTest() {
        val options = RetrieveAvailablePaymentMethodsOptions(
            clientSecret = "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            pageNum = 1,
            pageSize = 20,
            active = true,
            transactionCurrency = null,
            transactionMode = null,
            countryCode = null
        )

        assertEquals(
            "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            options.clientSecret
        )
        assertEquals(1, options.pageNum)
        assertEquals(20, options.pageSize)
        assertEquals(true, options.active)
        assertEquals(null, options.transactionCurrency)
        assertEquals(null, options.transactionMode)
    }

    @Test
    fun confirmPaymentIntentOptionsTest() {
        val options = Options.ConfirmPaymentIntentOptions(
            clientSecret = "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            paymentIntentId = "int_hkdmr7v9rg1j58ky8re",
            request = PaymentIntentConfirmRequest.Builder(
                requestId = "3d702b7b-ac7a-46b5-bf62-9fee0dd713bf"
            )
                .build()
        )
        assertEquals(
            "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            options.clientSecret
        )
        assertEquals("int_hkdmr7v9rg1j58ky8re", options.paymentIntentId)
        assertEquals(
            PaymentIntentConfirmRequest.Builder(
                requestId = "3d702b7b-ac7a-46b5-bf62-9fee0dd713bf"
            )
                .build(),
            options.request
        )
    }

    @Test
    fun retrievePaymentIntentOptionsTest() {
        val options = Options.RetrievePaymentIntentOptions(
            clientSecret = "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            paymentIntentId = "int_hkdmr7v9rg1j58ky8re"
        )
        assertEquals(
            "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            options.clientSecret
        )
        assertEquals("int_hkdmr7v9rg1j58ky8re", options.paymentIntentId)
    }

    @Test
    fun continuePaymentIntentOptionsTest() {
        val options = Options.ContinuePaymentIntentOptions(
            clientSecret = "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            paymentIntentId = "int_hkdmr7v9rg1j58ky8re",
            request = PaymentIntentContinueRequest(
                requestId = "3d702b7b-ac7a-46b5-bf62-9fee0dd713bf"
            )
        )
        assertEquals(
            "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            options.clientSecret
        )
        assertEquals("int_hkdmr7v9rg1j58ky8re", options.paymentIntentId)
        assertEquals(
            PaymentIntentContinueRequest(
                requestId = "3d702b7b-ac7a-46b5-bf62-9fee0dd713bf"
            ),
            options.request
        )
    }

    @Test
    fun createPaymentMethodOptionsTest() {
        val options = Options.CreatePaymentMethodOptions(
            clientSecret = "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            request = PaymentMethodCreateRequest(
                requestId = "3d702b7b-ac7a-46b5-bf62-9fee0dd713bf"
            )
        )
        assertEquals(
            "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            options.clientSecret
        )
        assertEquals(
            PaymentMethodCreateRequest(
                requestId = "3d702b7b-ac7a-46b5-bf62-9fee0dd713bf"
            ),
            options.request
        )
    }

    @Test
    fun createPaymentConsentOptionsTest() {
        val options = Options.CreatePaymentConsentOptions(
            clientSecret = "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            request = PaymentConsentCreateRequest(
                requestId = "3d702b7b-ac7a-46b5-bf62-9fee0dd713bf"
            )
        )
        assertEquals(
            "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            options.clientSecret
        )
        assertEquals(
            PaymentConsentCreateRequest(
                requestId = "3d702b7b-ac7a-46b5-bf62-9fee0dd713bf"
            ),
            options.request
        )
    }

    @Test
    fun verifyPaymentConsentOptionsTest() {
        val options = Options.VerifyPaymentConsentOptions(
            clientSecret = "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            paymentConsentId = "cst_hkdmr7v9rg1j5g4azy6",
            request = PaymentConsentVerifyRequest(
                requestId = "3d702b7b-ac7a-46b5-bf62-9fee0dd713bf"
            )
        )
        assertEquals(
            "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            options.clientSecret
        )
        assertEquals("cst_hkdmr7v9rg1j5g4azy6", options.paymentConsentId)
        assertEquals(
            PaymentConsentVerifyRequest(
                requestId = "3d702b7b-ac7a-46b5-bf62-9fee0dd713bf"
            ),
            options.request
        )
    }

    @Test
    fun disablePaymentConsentOptionsTest() {
        val options = Options.DisablePaymentConsentOptions(
            clientSecret = "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            paymentConsentId = "cst_hkdmr7v9rg1j5g4azy6",
            request = PaymentConsentDisableRequest(
                requestId = "3d702b7b-ac7a-46b5-bf62-9fee0dd713bf"
            )
        )
        assertEquals(
            "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            options.clientSecret
        )
        assertEquals("cst_hkdmr7v9rg1j5g4azy6", options.paymentConsentId)
        assertEquals(
            PaymentConsentDisableRequest(
                requestId = "3d702b7b-ac7a-46b5-bf62-9fee0dd713bf"
            ),
            options.request
        )
    }

    @Test
    fun retrievePaymentConsentOptionsTest() {
        val options = Options.RetrievePaymentConsentOptions(
            clientSecret = "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            paymentConsentId = "cst_hkdmr7v9rg1j5g4azy6"
        )
        assertEquals(
            "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            options.clientSecret
        )
        assertEquals("cst_hkdmr7v9rg1j5g4azy6", options.paymentConsentId)
    }

    @Test
    fun confirmPaymentIntentUrlTest() {
        val url =
            AirwallexApiRepository.confirmPaymentIntentUrl("https://api.airwallex.com", "abc")
        assertEquals("https://api.airwallex.com/api/v1/pa/payment_intents/abc/confirm", url)
    }

    @Test
    fun retrievePaymentIntentUrlTest() {
        val url =
            AirwallexApiRepository.retrievePaymentIntentUrl("https://api.airwallex.com", "abc")
        assertEquals("https://api.airwallex.com/api/v1/pa/payment_intents/abc", url)
    }

    @Test
    fun continuePaymentIntentUrlTest() {
        val url =
            AirwallexApiRepository.continuePaymentIntentUrl("https://api.airwallex.com", "abc")
        assertEquals(
            "https://api.airwallex.com/api/v1/pa/payment_intents/abc/confirm_continue",
            url
        )
    }

    @Test
    fun createPaymentMethodUrlTest() {
        val url =
            AirwallexApiRepository.createPaymentMethodUrl("https://api.airwallex.com")
        assertEquals("https://api.airwallex.com/api/v1/pa/payment_methods/create", url)
    }

    @Test
    fun createPaymentConsentUrlTest() {
        val url =
            AirwallexApiRepository.createPaymentConsentUrl("https://api.airwallex.com")
        assertEquals("https://api.airwallex.com/api/v1/pa/payment_consents/create", url)
    }

    @Test
    fun verifyPaymentConsentUrlTest() {
        val url =
            AirwallexApiRepository.verifyPaymentConsentUrl("https://api.airwallex.com", "abc")
        assertEquals("https://api.airwallex.com/api/v1/pa/payment_consents/abc/verify", url)
    }

    @Test
    fun disablePaymentConsentUrlTest() {
        val url =
            AirwallexApiRepository.disablePaymentConsentUrl("https://api.airwallex.com", "abc")
        assertEquals("https://api.airwallex.com/api/v1/pa/payment_consents/abc/disable", url)
    }

    @Test
    fun retrievePaymentConsentUrlTest() {
        val url =
            AirwallexApiRepository.retrievePaymentConsentUrl("https://api.airwallex.com", "abc")
        assertEquals("https://api.airwallex.com/api/v1/pa/payment_consents/abc", url)
    }

    @Test
    fun retrieveAvailablePaymentMethodsUrlTest() {
        mockUri()
        val url =
            AirwallexApiRepository.retrieveAvailablePaymentMethodsUrl(
                "https://api.airwallex.com",
                1,
                20,
                true,
                "CNY",
                TransactionMode.ONE_OFF,
                "CN"
            )
        assertEquals(
            "https://api.airwallex.com/api/v1/pa/config/payment_method_types?__resources=true&os_type=android&lang=en&page_num=1&page_size=20&active=true&transaction_currency=CNY&transaction_mode=oneoff&country_code=CN",
            url
        )
    }

    @Test
    fun getApiUrlTest() {
        val url =
            AirwallexApiRepository.getApiUrl("https://api.airwallex.com", "abc")
        assertEquals("https://api.airwallex.com/api/v1/pa/abc", url)
    }

    @Test
    fun getLanguageCode_variousLocales() {
        val testCases = listOf(
            Triple(Locale("en"), "en", "en"),
            Triple(Locale.Builder().setLanguage("zh").setScript("Hans").build(), "zh-Hans", "zh-Hans"),
            Triple(Locale.Builder().setLanguage("zh").setScript("Hant").build(), "zh-Hant", "zh-Hant"),
            Triple(Locale("pt", "BR"), "pt-BR", "pt-BR"),
            Triple(Locale("pt", "PT"), "pt-PT", "pt-PT"),
            Triple(Locale("fr"), "fr", "fr"),
            Triple(Locale("zh", "CN"), "zh", "zh"), // fallback if script not set
            Triple(Locale("pt"), "pt", "pt"),
            Triple(Locale("ja"), "ja", "ja"),
            Triple(Locale("ko"), "ko", "ko"),
            Triple(Locale("ru"), "ru", "ru"),
            Triple(Locale("th"), "th", "th")
        )
        val defaultLocale = Locale.getDefault()
        try {
            for ((locale, expected, description) in testCases) {
                Locale.setDefault(locale)
                val code = AirwallexApiRepository.getLanguageCode()
                assertEquals(expected, code, "Locale $description should return $expected but got $code")
            }
        } finally {
            Locale.setDefault(defaultLocale)
        }
    }

    @Test
    fun retrieveAvailablePaymentConsentsUrlTest_allParameters() {
        mockUri()
        val url = AirwallexApiRepository.retrieveAvailablePaymentConsentsUrl(
            baseUrl = "https://api.airwallex.com",
            customerId = "cus_123",
            merchantTriggerReason = PaymentConsent.MerchantTriggerReason.SCHEDULED,
            nextTriggeredBy = PaymentConsent.NextTriggeredBy.CUSTOMER,
            status = PaymentConsent.PaymentConsentStatus.VERIFIED,
            pageNum = 1,
            pageSize = 20
        )
        assertEquals(
            "https://api.airwallex.com/api/v1/pa/payment_consents?customer_id=cus_123&merchant_trigger_reason=scheduled&next_triggered_by=customer&status=VERIFIED&page_num=1&page_size=20",
            url
        )
    }

    @Test
    fun retrieveAvailablePaymentConsentsUrlTest_nullParameters() {
        mockUri()
        val url = AirwallexApiRepository.retrieveAvailablePaymentConsentsUrl(
            baseUrl = "https://api.airwallex.com",
            customerId = null,
            merchantTriggerReason = null,
            nextTriggeredBy = null,
            status = null,
            pageNum = null,
            pageSize = null
        )
        assertEquals(
            "https://api.airwallex.com/api/v1/pa/payment_consents",
            url
        )
    }

    @Test
    fun retrievePaymentMethodTypeInfoUrlTest_allParameters() {
        mockUri()
        val defaultLocale = Locale.getDefault()
        try {
            Locale.setDefault(Locale("en"))
            val url = AirwallexApiRepository.retrievePaymentMethodTypeInfoUrl(
                baseUrl = "https://api.airwallex.com",
                paymentMethodType = "card",
                countryCode = "US",
                flow = AirwallexPaymentRequestFlow.IN_APP,
                openId = "open_123"
            )
            assertEquals(
                "https://api.airwallex.com/api/v1/pa/config/payment_method_types/card?country_code=US&flow=inapp&open_id=open_123&os_type=android&lang=en",
                url
            )
        } finally {
            Locale.setDefault(defaultLocale)
        }
    }

    @Test
    fun retrievePaymentMethodTypeInfoUrlTest_nullParameters() {
        mockUri()
        val defaultLocale = Locale.getDefault()
        try {
            Locale.setDefault(Locale("en"))
            val url = AirwallexApiRepository.retrievePaymentMethodTypeInfoUrl(
                baseUrl = "https://api.airwallex.com",
                paymentMethodType = "wechatpay",
                countryCode = null,
                flow = null,
                openId = null
            )
            assertEquals(
                "https://api.airwallex.com/api/v1/pa/config/payment_method_types/wechatpay?os_type=android&lang=en",
                url
            )
        } finally {
            Locale.setDefault(defaultLocale)
        }
    }

    @Test
    fun retrieveBanksUrlTest_allParameters() {
        mockUri()
        val defaultLocale = Locale.getDefault()
        try {
            Locale.setDefault(Locale("en"))
            val url = AirwallexApiRepository.retrieveBanksUrl(
                baseUrl = "https://api.airwallex.com",
                paymentMethodType = "online_banking",
                countryCode = "TH",
                flow = AirwallexPaymentRequestFlow.IN_APP,
                openId = "open_456"
            )
            assertEquals(
                "https://api.airwallex.com/api/v1/pa/config/banks?payment_method_type=online_banking&__all_logos=true&country_code=TH&flow=inapp&open_id=open_456&os_type=android&lang=en",
                url
            )
        } finally {
            Locale.setDefault(defaultLocale)
        }
    }

    @Test
    fun retrieveBanksUrlTest_nullParameters() {
        mockUri()
        val defaultLocale = Locale.getDefault()
        try {
            Locale.setDefault(Locale("en"))
            val url = AirwallexApiRepository.retrieveBanksUrl(
                baseUrl = "https://api.airwallex.com",
                paymentMethodType = "online_banking",
                countryCode = null,
                flow = null,
                openId = null
            )
            assertEquals(
                "https://api.airwallex.com/api/v1/pa/config/banks?payment_method_type=online_banking&__all_logos=true&os_type=android&lang=en",
                url
            )
        } finally {
            Locale.setDefault(defaultLocale)
        }
    }

    @Test
    fun retrieveAvailablePaymentMethods_success() = runBlocking<Unit> {
        mockUri()
        val responseJson = """
            {
                "items": [
                    {
                        "name": "card",
                        "transaction_mode": "oneoff"
                    }
                ],
                "has_more": false
            }
        """.trimIndent()

        coEvery { anyConstructed<AirwallexHttpClient>().execute(any<AirwallexHttpRequest>()) } returns
            AirwallexHttpResponse(200, responseJson)

        val options = RetrieveAvailablePaymentMethodsOptions(
            clientSecret = "test_secret",
            pageNum = 0,
            pageSize = 20,
            active = true,
            transactionCurrency = null,
            transactionMode = null,
            countryCode = null
        )

        val result = apiRepository.retrieveAvailablePaymentMethods(options)
        assertNotNull(result)
    }

    @Test
    fun confirmPaymentIntent_badRequest() = runBlocking<Unit> {
        val errorJson = """
            {
                "error": {
                    "code": "invalid_request",
                    "message": "Invalid payment intent"
                }
            }
        """.trimIndent()

        coEvery { anyConstructed<AirwallexHttpClient>().execute(any<AirwallexHttpRequest>()) } returns
            AirwallexHttpResponse(HttpURLConnection.HTTP_BAD_REQUEST, errorJson)

        val options = Options.ConfirmPaymentIntentOptions(
            clientSecret = "test_secret",
            paymentIntentId = "int_123",
            request = PaymentIntentConfirmRequest.Builder("req_123").build()
        )

        assertFailsWith<InvalidRequestException> {
            apiRepository.confirmPaymentIntent(options)
        }
    }

    @Test
    fun retrievePaymentIntent_unauthorized() = runBlocking<Unit> {
        val errorJson = """
            {
                "error": {
                    "code": "unauthorized",
                    "message": "Invalid client secret"
                }
            }
        """.trimIndent()

        coEvery { anyConstructed<AirwallexHttpClient>().execute(any<AirwallexHttpRequest>()) } returns
            AirwallexHttpResponse(HttpURLConnection.HTTP_UNAUTHORIZED, errorJson)

        val options = Options.RetrievePaymentIntentOptions(
            clientSecret = "invalid_secret",
            paymentIntentId = "int_123"
        )

        assertFailsWith<AuthenticationException> {
            apiRepository.retrievePaymentIntent(options)
        }
    }

    @Test
    fun createPaymentMethod_forbidden() = runBlocking<Unit> {
        val errorJson = """
            {
                "error": {
                    "code": "forbidden",
                    "message": "Permission denied"
                }
            }
        """.trimIndent()

        coEvery { anyConstructed<AirwallexHttpClient>().execute(any<AirwallexHttpRequest>()) } returns
            AirwallexHttpResponse(HttpURLConnection.HTTP_FORBIDDEN, errorJson)

        val options = Options.CreatePaymentMethodOptions(
            clientSecret = "test_secret",
            request = PaymentMethodCreateRequest("req_123")
        )

        assertFailsWith<PermissionException> {
            apiRepository.createPaymentMethod(options)
        }
    }

    @Test
    fun continuePaymentIntent_ioException() = runBlocking<Unit> {
        coEvery { anyConstructed<AirwallexHttpClient>().execute(any<AirwallexHttpRequest>()) } throws
            IOException("Network error")

        val options = Options.ContinuePaymentIntentOptions(
            clientSecret = "test_secret",
            paymentIntentId = "int_123",
            request = PaymentIntentContinueRequest("req_123")
        )

        assertFailsWith<APIConnectionException> {
            apiRepository.continuePaymentIntent(options)
        }
    }

    @Test
    fun createPaymentConsent_success() = runBlocking<Unit> {
        val responseJson = """
            {
                "id": "cst_123",
                "request_id": "req_123",
                "status": "VERIFIED"
            }
        """.trimIndent()

        coEvery { anyConstructed<AirwallexHttpClient>().execute(any<AirwallexHttpRequest>()) } returns
            AirwallexHttpResponse(200, responseJson)

        val options = Options.CreatePaymentConsentOptions(
            clientSecret = "test_secret",
            request = PaymentConsentCreateRequest("req_123")
        )

        val result = apiRepository.createPaymentConsent(options)
        assertNotNull(result)
    }

    @Test
    fun verifyPaymentConsent_success() = runBlocking<Unit> {
        val responseJson = """
            {
                "id": "cst_123",
                "status": "VERIFIED"
            }
        """.trimIndent()

        coEvery { anyConstructed<AirwallexHttpClient>().execute(any<AirwallexHttpRequest>()) } returns
            AirwallexHttpResponse(200, responseJson)

        val options = Options.VerifyPaymentConsentOptions(
            clientSecret = "test_secret",
            paymentConsentId = "cst_123",
            request = PaymentConsentVerifyRequest("req_123")
        )

        val result = apiRepository.verifyPaymentConsent(options)
        assertNotNull(result)
    }

    @Test
    fun disablePaymentConsent_success() = runBlocking<Unit> {
        val responseJson = """
            {
                "id": "cst_123",
                "status": "DISABLED"
            }
        """.trimIndent()

        coEvery { anyConstructed<AirwallexHttpClient>().execute(any<AirwallexHttpRequest>()) } returns
            AirwallexHttpResponse(200, responseJson)

        val options = Options.DisablePaymentConsentOptions(
            clientSecret = "test_secret",
            paymentConsentId = "cst_123",
            request = PaymentConsentDisableRequest("req_123")
        )

        val result = apiRepository.disablePaymentConsent(options)
        assertNotNull(result)
    }

    @Test
    fun retrievePaymentConsent_success() = runBlocking<Unit> {
        val responseJson = """
            {
                "id": "cst_123",
                "status": "VERIFIED",
                "customer_id": "cus_123"
            }
        """.trimIndent()

        coEvery { anyConstructed<AirwallexHttpClient>().execute(any<AirwallexHttpRequest>()) } returns
            AirwallexHttpResponse(200, responseJson)

        val options = Options.RetrievePaymentConsentOptions(
            clientSecret = "test_secret",
            paymentConsentId = "cst_123"
        )

        val result = apiRepository.retrievePaymentConsent(options)
        assertNotNull(result)
    }

    @Test
    fun retrieveAvailablePaymentConsents_success() = runBlocking<Unit> {
        mockUri()
        val responseJson = """
            {
                "items": [
                    {
                        "id": "cst_123",
                        "status": "VERIFIED"
                    }
                ],
                "has_more": false
            }
        """.trimIndent()

        coEvery { anyConstructed<AirwallexHttpClient>().execute(any<AirwallexHttpRequest>()) } returns
            AirwallexHttpResponse(200, responseJson)

        val options = Options.RetrieveAvailablePaymentConsentsOptions(
            clientSecret = "test_secret",
            customerId = "cus_123",
            merchantTriggerReason = null,
            nextTriggeredBy = null,
            status = null,
            pageNum = 0,
            pageSize = 20
        )

        val result = apiRepository.retrieveAvailablePaymentConsents(options)
        assertNotNull(result)
    }

    @Test
    fun retrievePaymentMethodTypeInfo_success() = runBlocking<Unit> {
        mockUri()
        val responseJson = """
            {
                "name": "card",
                "display_name": "Card",
                "has_schema": true
            }
        """.trimIndent()

        coEvery { anyConstructed<AirwallexHttpClient>().execute(any<AirwallexHttpRequest>()) } returns
            AirwallexHttpResponse(200, responseJson)

        val options = Options.RetrievePaymentMethodTypeInfoOptions(
            clientSecret = "test_secret",
            paymentMethodType = "card",
            flow = null,
            transactionMode = null,
            countryCode = null,
            openId = null
        )

        val result = apiRepository.retrievePaymentMethodTypeInfo(options)
        assertNotNull(result)
    }

    @Test
    fun retrieveBanks_success() = runBlocking<Unit> {
        mockUri()
        val responseJson = """
            {
                "items": [
                    {
                        "name": "Test Bank",
                        "bank_code": "TEST"
                    }
                ]
            }
        """.trimIndent()

        coEvery { anyConstructed<AirwallexHttpClient>().execute(any<AirwallexHttpRequest>()) } returns
            AirwallexHttpResponse(200, responseJson)

        val options = Options.RetrieveBankOptions(
            clientSecret = "test_secret",
            paymentMethodType = "online_banking",
            flow = null,
            transactionMode = null,
            countryCode = "TH",
            openId = null
        )

        val result = apiRepository.retrieveBanks(options)
        assertNotNull(result)
    }

    @Test
    fun handleApiError_internalServerError() = runBlocking<Unit> {
        val errorJson = """
            {
                "error": {
                    "code": "internal_error",
                    "message": "Internal server error"
                }
            }
        """.trimIndent()

        coEvery { anyConstructed<AirwallexHttpClient>().execute(any<AirwallexHttpRequest>()) } returns
            AirwallexHttpResponse(HttpURLConnection.HTTP_INTERNAL_ERROR, errorJson)

        val options = Options.RetrievePaymentIntentOptions(
            clientSecret = "test_secret",
            paymentIntentId = "int_123"
        )

        val exception = assertFailsWith<com.airwallex.android.core.exception.APIException> {
            apiRepository.retrievePaymentIntent(options)
        }
        assertEquals(HttpURLConnection.HTTP_INTERNAL_ERROR, exception.statusCode)
    }

    @Test
    fun handleApiError_notFound() = runBlocking<Unit> {
        val errorJson = """
            {
                "error": {
                    "code": "resource_not_found",
                    "message": "Resource not found"
                }
            }
        """.trimIndent()

        coEvery { anyConstructed<AirwallexHttpClient>().execute(any<AirwallexHttpRequest>()) } returns
            AirwallexHttpResponse(HttpURLConnection.HTTP_NOT_FOUND, errorJson)

        val options = Options.RetrievePaymentIntentOptions(
            clientSecret = "test_secret",
            paymentIntentId = "int_notfound"
        )

        assertFailsWith<InvalidRequestException> {
            apiRepository.retrievePaymentIntent(options)
        }
    }

    private fun mockUri() {
        // Mock Uri.parse() to avoid android.net.Uri not mocked error
        mockkStatic("android.net.Uri")
        every { Uri.parse(any()) } answers {
            val url = firstArg<String>()
            val params = mutableMapOf<String, String>()

            val builder = mockk<Uri.Builder>(relaxed = true)
            every { builder.appendQueryParameter(any(), any()) } answers {
                params[firstArg()] = secondArg()
                builder
            }
            every { builder.build() } answers {
                val builtUri = mockk<Uri>()
                every { builtUri.toString() } answers {
                    val queryString = params.entries.joinToString("&") { "${it.key}=${it.value}" }
                    if (queryString.isNotEmpty()) "$url?$queryString" else url
                }
                builtUri
            }

            val uri = mockk<Uri>()
            every { uri.buildUpon() } returns builder
            uri
        }
    }
}
