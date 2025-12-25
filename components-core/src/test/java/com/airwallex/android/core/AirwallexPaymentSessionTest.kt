package com.airwallex.android.core

import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PaymentIntentFixtures
import com.airwallex.android.core.model.Shipping
import io.mockk.every
import io.mockk.just
import io.mockk.mockkObject
import io.mockk.runs
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AirwallexPaymentSessionTest {

    @Before
    fun setup() {
        mockkObject(TokenManager)
        every { TokenManager.updateClientSecret(any()) } just runs
    }

    @Test
    fun buildTest() {
        val shipping = Shipping.Builder()
            .setFirstName("John")
            .setLastName("Doe")
            .build()

        val airwallexPaymentSession = AirwallexPaymentSession.Builder(
            PaymentIntentFixtures.PAYMENT_INTENT, "CN"
        )
            .setRequireBillingInformation(false)
            .setRequireEmail(true)
            .setReturnUrl("airwallexcheckout://com.airwallex.paymentacceptance")
            .setPaymentMethods(listOf("googlepay"))
            .setHidePaymentConsents(false)
            .setAutoCapture(false)
            .setShipping(shipping)
            .build()

        assertNotNull(airwallexPaymentSession)

        assertNotNull(airwallexPaymentSession.currency)
        assertEquals("AUD", airwallexPaymentSession.currency)

        assertNotNull(airwallexPaymentSession.amount)
        assertEquals(BigDecimal.valueOf(100.01), airwallexPaymentSession.amount)

        assertNotNull(airwallexPaymentSession.isBillingInformationRequired)
        assertEquals(false, airwallexPaymentSession.isBillingInformationRequired)

        assertTrue(airwallexPaymentSession.isEmailRequired)

        assertEquals("cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu", airwallexPaymentSession.customerId)
        assertEquals("airwallexcheckout://com.airwallex.paymentacceptance", airwallexPaymentSession.returnUrl)
        assertEquals(false, airwallexPaymentSession.autoCapture)
        assertEquals(false, airwallexPaymentSession.hidePaymentConsents)
        assertNotNull(airwallexPaymentSession.paymentMethods)
        assertEquals(airwallexPaymentSession.shipping, shipping)
    }

    @Test
    fun `build with PaymentIntentProvider`() {
        val testProvider = TestPaymentIntentProvider(
            currency = "USD",
            amount = BigDecimal(50.0)
        )

        val airwallexPaymentSession = AirwallexPaymentSession.Builder(
            paymentIntentProvider = testProvider,
            countryCode = "US",
            customerId = "test_customer"
        ).build()

        assertNotNull(airwallexPaymentSession)
        assertEquals("USD", airwallexPaymentSession.currency)
        assertEquals(BigDecimal(50.0), airwallexPaymentSession.amount)
        assertEquals("test_customer", airwallexPaymentSession.customerId)
        assertNotNull(airwallexPaymentSession.paymentIntentProvider)
    }

    @Test
    fun `build with PaymentIntentProvider without customerId`() {
        val testProvider = TestPaymentIntentProvider(
            currency = "USD",
            amount = BigDecimal(50.0)
        )

        val airwallexPaymentSession = AirwallexPaymentSession.Builder(
            paymentIntentProvider = testProvider,
            countryCode = "US"
        ).build()

        assertNotNull(airwallexPaymentSession)
        assertEquals("USD", airwallexPaymentSession.currency)
        assertEquals(BigDecimal(50.0), airwallexPaymentSession.amount)
        assertEquals(null, airwallexPaymentSession.customerId)
        assertNotNull(airwallexPaymentSession.paymentIntentProvider)
    }

    @Test
    fun `build with PaymentIntentSource`() {
        val testSource = TestPaymentIntentSource(
            currency = "EUR",
            amount = BigDecimal(75.0)
        )

        val airwallexPaymentSession = AirwallexPaymentSession.Builder(
            paymentIntentSource = testSource,
            countryCode = "DE",
            customerId = "test_customer_de"
        ).build()

        assertNotNull(airwallexPaymentSession)
        assertEquals("EUR", airwallexPaymentSession.currency)
        assertEquals(BigDecimal(75.0), airwallexPaymentSession.amount)
        assertEquals("test_customer_de", airwallexPaymentSession.customerId)
        assertNotNull(airwallexPaymentSession.paymentIntentProvider)
    }

    @Test
    fun `build with PaymentIntentSource without customerId`() {
        val testSource = TestPaymentIntentSource(
            currency = "EUR",
            amount = BigDecimal(75.0)
        )

        val airwallexPaymentSession = AirwallexPaymentSession.Builder(
            paymentIntentSource = testSource,
            countryCode = "DE"
        ).build()

        assertNotNull(airwallexPaymentSession)
        assertEquals("EUR", airwallexPaymentSession.currency)
        assertEquals(BigDecimal(75.0), airwallexPaymentSession.amount)
        assertEquals(null, airwallexPaymentSession.customerId)
        assertNotNull(airwallexPaymentSession.paymentIntentProvider)
    }

    private class TestPaymentIntentProvider(
        override val currency: String,
        override val amount: BigDecimal
    ) : PaymentIntentProvider {
        override fun provide(callback: PaymentIntentProvider.PaymentIntentCallback) {
            callback.onSuccess(PaymentIntentFixtures.PAYMENT_INTENT)
        }
    }

    private class TestPaymentIntentSource(
        override val currency: String,
        override val amount: BigDecimal
    ) : PaymentIntentSource {
        override suspend fun getPaymentIntent(): PaymentIntent {
            return PaymentIntentFixtures.PAYMENT_INTENT
        }
    }

    @Test
    fun `isExpressCheckout returns false when session has no PaymentIntentProvider`() {
        val session = AirwallexPaymentSession.Builder(
            PaymentIntentFixtures.PAYMENT_INTENT, "CN"
        ).build()

        assertEquals(false, session.isExpressCheckout)
    }

    @Test
    fun `isExpressCheckout returns true when session has PaymentIntentProvider`() {
        val testProvider = TestPaymentIntentProvider(
            currency = "USD",
            amount = BigDecimal(50.0)
        )

        val session = AirwallexPaymentSession.Builder(
            paymentIntentProvider = testProvider,
            countryCode = "US",
            customerId = "test_customer"
        ).build()

        assertEquals(true, session.isExpressCheckout)
    }

    @Test
    fun `isExpressCheckout returns true when session has PaymentIntentSource`() {
        val testSource = TestPaymentIntentSource(
            currency = "EUR",
            amount = BigDecimal(75.0)
        )

        val session = AirwallexPaymentSession.Builder(
            paymentIntentSource = testSource,
            countryCode = "DE"
        ).build()

        assertEquals(true, session.isExpressCheckout)
    }

    @Test
    fun `isExpressCheckout returns true when paymentIntentProviderId is set`() {
        val session = AirwallexPaymentSession.Builder(
            PaymentIntentFixtures.PAYMENT_INTENT, "CN"
        ).build()

        // Simulate binding to activity which sets paymentIntentProviderId
        session.paymentIntentProviderId = "test-provider-id"

        assertEquals(true, session.isExpressCheckout)
    }
}
