package com.airwallex.android.core

import com.airwallex.android.core.http.AirwallexHttpClient
import com.airwallex.android.core.http.AirwallexHttpRequest
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.model.Options
import com.airwallex.android.core.model.Options.RetrieveAvailablePaymentMethodsOptions
import com.airwallex.android.core.model.PaymentConsentCreateRequest
import com.airwallex.android.core.model.PaymentConsentDisableRequest
import com.airwallex.android.core.model.PaymentConsentVerifyRequest
import com.airwallex.android.core.model.PaymentIntentConfirmRequest
import com.airwallex.android.core.model.PaymentIntentContinueRequest
import com.airwallex.android.core.model.PaymentMethodCreateRequest
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
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
    fun executeMockWeChatLogsDebugMessageOnFailure() = runBlocking {
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
        val url =
            AirwallexApiRepository.retrieveAvailablePaymentMethodsUrl(
                "https://api.airwallex.com",
                1,
                20,
                true,
                "CNY",
                null,
                null
            )
        assertEquals(
            "https://api.airwallex.com/api/v1/pa/config/payment_method_types?__resources=true&os_type=android&lang=en&page_num=1&page_size=20&active=true&transaction_currency=CNY",
            url
        )
    }

    @Test
    fun getApiUrlTest() {
        val url =
            AirwallexApiRepository.getApiUrl("https://api.airwallex.com", "abc")
        assertEquals("https://api.airwallex.com/api/v1/pa/abc", url)
    }
}
