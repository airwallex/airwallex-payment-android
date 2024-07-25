package com.airwallex.android.core.extension

import android.net.Uri
import com.airwallex.android.core.AirwallexPlugins
import com.airwallex.android.core.Environment
import com.airwallex.android.core.http.AirwallexHttpRequest
import com.airwallex.android.core.model.*
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OptionsExtensionsTest {
    private val clientSecret = "qadf"
    private val intentId = "id"
    private val consentId = "cid"
    private val requestId = "rid"
    private val mockUrl = "http://abc.com"

    @Before
    fun setUp() {
        mockkStatic(Uri::class)
        mockkObject(AirwallexPlugins)

        every { AirwallexPlugins.environment } returns Environment.PRODUCTION
        val mockUri = mockk<Uri>()
        every { mockUri.toString() } returns mockUrl
        val mockBuilder = mockk<Uri.Builder>()
        every { mockBuilder.build() } returns mockUri
        every { mockBuilder.appendQueryParameter(any(), any()) } returns mockBuilder
        every { Uri.parse(any()).buildUpon() } returns mockBuilder
    }

    @After
    fun unmock() {
        unmockkAll()
    }

    @Test
    fun `test ConfirmPaymentIntentOptions toAirwallexHttpRequest`() {
        val option = Options.ConfirmPaymentIntentOptions(
            clientSecret,
            intentId,
            PaymentIntentConfirmRequest(
                requestId
            )
        )
        val request = option.toAirwallexHttpRequest()
        assertEquals(request.method, AirwallexHttpRequest.Method.POST)
        assertEquals(request.url, "https://api.airwallex.com/api/v1/pa/payment_intents/id/confirm")
        assertEquals(request.options, option)
        assertTrue(request.params?.containsValue(requestId) == true)
    }

    @Test
    fun `test ContinuePaymentIntentOptions toAirwallexHttpRequest`() {
        val option = Options.ContinuePaymentIntentOptions(
            clientSecret,
            intentId,
            PaymentIntentContinueRequest(
                requestId
            )
        )
        val request = option.toAirwallexHttpRequest()
        assertEquals(request.method, AirwallexHttpRequest.Method.POST)
        assertEquals(
            request.url,
            "https://api.airwallex.com/api/v1/pa/payment_intents/id/confirm_continue"
        )
        assertEquals(request.options, option)
        assertTrue(request.params?.containsValue(requestId) == true)
    }

    @Test
    fun `test RetrievePaymentIntentOptions toAirwallexHttpRequest`() {
        val option = Options.RetrievePaymentIntentOptions(clientSecret, intentId)
        val request = option.toAirwallexHttpRequest()
        assertEquals(request.method, AirwallexHttpRequest.Method.GET)
        assertEquals(request.url, "https://api.airwallex.com/api/v1/pa/payment_intents/id")
        assertEquals(request.options, option)
        assertNull(request.params)
    }

    @Test
    fun `test CreatePaymentMethodOptions toAirwallexHttpRequest`() {
        val option =
            Options.CreatePaymentMethodOptions(clientSecret, PaymentMethodCreateRequest(requestId))
        val request = option.toAirwallexHttpRequest()
        assertEquals(request.method, AirwallexHttpRequest.Method.POST)
        assertEquals(request.url, "https://api.airwallex.com/api/v1/pa/payment_methods/create")
        assertEquals(request.options, option)
        assertTrue(request.params?.containsValue(requestId) == true)
    }

    @Test
    fun `test CreatePaymentConsentOptions toAirwallexHttpRequest`() {
        val option = Options.CreatePaymentConsentOptions(
            clientSecret,
            PaymentConsentCreateRequest(requestId)
        )
        val request = option.toAirwallexHttpRequest()
        assertEquals(request.method, AirwallexHttpRequest.Method.POST)
        assertEquals(request.url, "https://api.airwallex.com/api/v1/pa/payment_consents/create")
        assertEquals(request.options, option)
        assertTrue(request.params?.containsValue(requestId) == true)
    }

    @Test
    fun `test VerifyPaymentConsentOptions toAirwallexHttpRequest`() {
        val option = Options.VerifyPaymentConsentOptions(
            clientSecret,
            consentId,
            PaymentConsentVerifyRequest(requestId)
        )
        val request = option.toAirwallexHttpRequest()
        assertEquals(request.method, AirwallexHttpRequest.Method.POST)
        assertEquals(request.url, "https://api.airwallex.com/api/v1/pa/payment_consents/cid/verify")
        assertEquals(request.options, option)
        assertTrue(request.params?.containsValue(requestId) == true)
    }

    @Test
    fun `test DisablePaymentConsentOptions toAirwallexHttpRequest`() {
        val option = Options.DisablePaymentConsentOptions(
            clientSecret,
            consentId,
            PaymentConsentDisableRequest(requestId)
        )
        val request = option.toAirwallexHttpRequest()
        assertEquals(request.method, AirwallexHttpRequest.Method.POST)
        assertEquals(
            request.url,
            "https://api.airwallex.com/api/v1/pa/payment_consents/cid/disable"
        )
        assertEquals(request.options, option)
        assertTrue(request.params?.containsValue(requestId) == true)
    }

    @Test
    fun `test RetrieveAvailablePaymentMethodsOptions toAirwallexHttpRequest`() {
        val option = Options.RetrieveAvailablePaymentMethodsOptions(
            clientSecret,
            0,
            5,
            null,
            null,
            null,
            null
        )
        val request = option.toAirwallexHttpRequest()
        assertEquals(request.method, AirwallexHttpRequest.Method.GET)
        assertEquals(request.url, mockUrl)
        assertEquals(request.options, option)
        assertNull(request.params)
    }

    @Test
    fun `test RetrieveBankOptions toAirwallexHttpRequest`() {
        val option = Options.RetrieveBankOptions(
            clientSecret,
            PaymentMethodType.CARD.value,
            null,
            null,
            null,
            null
        )
        val request = option.toAirwallexHttpRequest()
        assertEquals(request.method, AirwallexHttpRequest.Method.GET)
        assertEquals(request.url, mockUrl)
        assertEquals(request.options, option)
        assertNull(request.params)
    }

    @Test
    fun `test RetrievePaymentConsentOptions toAirwallexHttpRequest`() {
        val option = Options.RetrievePaymentConsentOptions(clientSecret, consentId)
        val request = option.toAirwallexHttpRequest()
        assertEquals(request.method, AirwallexHttpRequest.Method.GET)
        assertEquals(request.url, "https://api.airwallex.com/api/v1/pa/payment_intents/cid")
        assertEquals(request.options, option)
        assertNull(request.params)
    }

    @Test
    fun `test RetrievePaymentMethodTypeInfoOptions toAirwallexHttpRequest`() {
        val option = Options.RetrievePaymentMethodTypeInfoOptions(
            clientSecret,
            PaymentMethodType.CARD.value,
            null,
            null,
            null,
            null
        )

        val request = option.toAirwallexHttpRequest()
        assertEquals(request.method, AirwallexHttpRequest.Method.GET)
        assertEquals(request.url, mockUrl)
        assertEquals(request.options, option)
        assertNull(request.params)
    }

    @Test
    fun `test RetrieveAvailablePaymentConsentsOptions toAirwallexHttpRequest`() {
        val option = Options.RetrieveAvailablePaymentConsentsOptions(
            clientSecret,
            "cusid",
            null,
            null,
            null,
            0,
            5
        )
        val request = option.toAirwallexHttpRequest()
        assertEquals(request.method, AirwallexHttpRequest.Method.GET)
        assertEquals(request.url, mockUrl)
        assertEquals(request.options, option)
    }
}