package com.airwallex.android.ui.extension

import com.airwallex.android.core.GooglePayOptions
import com.airwallex.android.core.Session
import com.airwallex.android.core.model.PaymentConsentOptions
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.Shipping
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SessionExtensionsTest {

    @Test
    fun `toParcelableSession maps all fields correctly`() {
        val paymentIntent = mockk<PaymentIntent>(relaxed = true)
        val shipping = mockk<Shipping>(relaxed = true)
        val googlePayOptions = GooglePayOptions()
        val paymentConsentOptions = mockk<PaymentConsentOptions>(relaxed = true)
        val paymentMethods = listOf("card", "googlepay")

        val session = mockk<Session>(relaxed = true) {
            every { this@mockk.paymentIntent } returns paymentIntent
            every { paymentIntentProviderId } returns "provider-123"
            every { this@mockk.paymentConsentOptions } returns paymentConsentOptions
            every { currency } returns "USD"
            every { countryCode } returns "US"
            every { amount } returns BigDecimal("99.99")
            every { this@mockk.shipping } returns shipping
            every { isBillingInformationRequired } returns true
            every { isEmailRequired } returns true
            every { customerId } returns "cus_123"
            every { returnUrl } returns "https://return.url"
            every { this@mockk.googlePayOptions } returns googlePayOptions
            every { this@mockk.paymentMethods } returns paymentMethods
            every { autoCapture } returns false
            every { hidePaymentConsents } returns true
        }

        val result = session.toParcelableSession()

        assertEquals(paymentIntent, result.paymentIntent)
        assertEquals("provider-123", result.paymentIntentProviderId)
        assertEquals(paymentConsentOptions, result.paymentConsentOptions)
        assertEquals("USD", result.currency)
        assertEquals("US", result.countryCode)
        assertEquals(BigDecimal("99.99"), result.amount)
        assertEquals(shipping, result.shipping)
        assertEquals(true, result.isBillingInformationRequired)
        assertEquals(true, result.isEmailRequired)
        assertEquals("cus_123", result.customerId)
        assertEquals("https://return.url", result.returnUrl)
        assertEquals(googlePayOptions, result.googlePayOptions)
        assertEquals(paymentMethods, result.paymentMethods)
        assertEquals(false, result.autoCapture)
        assertEquals(true, result.hidePaymentConsents)
    }

    @Test
    fun `toParcelableSession handles null optional fields`() {
        val session = mockk<Session>(relaxed = true) {
            every { paymentIntent } returns null
            every { paymentIntentProviderId } returns null
            every { paymentConsentOptions } returns null
            every { currency } returns "HKD"
            every { countryCode } returns "HK"
            every { amount } returns BigDecimal.ZERO
            every { shipping } returns null
            every { isBillingInformationRequired } returns false
            every { isEmailRequired } returns false
            every { customerId } returns null
            every { returnUrl } returns null
            every { googlePayOptions } returns null
            every { paymentMethods } returns null
            every { autoCapture } returns true
            every { hidePaymentConsents } returns false
        }

        val result = session.toParcelableSession()

        assertNull(result.paymentIntent)
        assertNull(result.paymentIntentProviderId)
        assertNull(result.paymentConsentOptions)
        assertEquals("HKD", result.currency)
        assertEquals("HK", result.countryCode)
        assertEquals(BigDecimal.ZERO, result.amount)
        assertNull(result.shipping)
        assertEquals(false, result.isBillingInformationRequired)
        assertEquals(false, result.isEmailRequired)
        assertNull(result.customerId)
        assertNull(result.returnUrl)
        assertNull(result.googlePayOptions)
        assertNull(result.paymentMethods)
        assertEquals(true, result.autoCapture)
        assertEquals(false, result.hidePaymentConsents)
    }
}
