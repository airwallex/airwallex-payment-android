package com.airwallex.android.core

import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class AirwallexApiRepositoryTest {

    @Test
    fun retrievePaResBaseUrlTest() {
        val url =
            AirwallexApiRepository.retrievePaResBaseUrl()
        assertEquals("https://pci-api.airwallex.com/pa/webhook/cybs", url)
    }

    @Test
    fun paResRetrieveUrlTest() {
        val url =
            AirwallexApiRepository.paResRetrieveUrl("abc")
        assertEquals("https://pci-api.airwallex.com/pa/webhook/cybs//paresCache?paResId=abc", url)
    }

    @Test
    fun paResTermUrlTest() {
        val url =
            AirwallexApiRepository.paResTermUrl()
        assertEquals("https://pci-api.airwallex.com/pa/webhook/cybs/pares/callback", url)
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
        assertEquals("https://api.airwallex.com/api/v1/pa/payment_intents/abc/confirm_continue", url)
    }

    @Test
    fun createPaymentMethodUrlTest() {
        val url =
            AirwallexApiRepository.createPaymentMethodUrl("https://api.airwallex.com")
        assertEquals("https://api.airwallex.com/api/v1/pa/payment_methods/create", url)
    }

    @Test
    fun trackerUrlTest() {
        val url =
            AirwallexApiRepository.trackerUrl()
        assertEquals("https://pci-api.airwallex.com/api/v1/checkout/collect", url)
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
            AirwallexApiRepository.retrieveAvailablePaymentMethodsUrl("https://api.airwallex.com", 1, 20, true, "CNY", null)
        assertEquals("https://api.airwallex.com/api/v1/pa/config/payment_method_types?page_num=1&page_size=20&active=true&transaction_currency=CNY", url)
    }

    @Test
    fun getApiUrlTest() {
        val url =
            AirwallexApiRepository.getApiUrl("https://api.airwallex.com", "abc")
        assertEquals("https://api.airwallex.com/api/v1/pa/abc", url)
    }
}
