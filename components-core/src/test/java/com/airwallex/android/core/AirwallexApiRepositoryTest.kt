package com.airwallex.android.core

import com.airwallex.android.core.AirwallexApiRepository.RetrieveAvailablePaymentMethodsOptions
import com.airwallex.android.core.model.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class AirwallexApiRepositoryTest {

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
        val options = AirwallexApiRepository.ConfirmPaymentIntentOptions(
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
        val options = AirwallexApiRepository.RetrievePaymentIntentOptions(
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
        val options = AirwallexApiRepository.ContinuePaymentIntentOptions(
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
        val options = AirwallexApiRepository.CreatePaymentMethodOptions(
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
        val options = AirwallexApiRepository.CreatePaymentConsentOptions(
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
        val options = AirwallexApiRepository.VerifyPaymentConsentOptions(
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
        val options = AirwallexApiRepository.DisablePaymentConsentOptions(
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
        val options = AirwallexApiRepository.RetrievePaymentConsentOptions(
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
    fun trackerOptionsTest() {
        val options = AirwallexApiRepository.TrackerOptions(
            request = TrackerRequest()
        )
        assertEquals(TrackerRequest(), options.request)
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
