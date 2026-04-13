package com.airwallex.android.core

import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PaymentIntentFixtures
import com.airwallex.android.core.model.Shipping
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.verify
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AirwallexPaymentSessionTest {

    @Before
    fun setup() {
        mockkObject(TokenManager)
        clearMocks(TokenManager)
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
    // TODO: check todo in init block of AirwallexPaymentSession
    @Ignore
    @Test
    fun `init block calls TokenManager when clientSecret is not null`() {
        val paymentIntent = PaymentIntentFixtures.PAYMENT_INTENT
        val clientSecret = paymentIntent.clientSecret
        assertNotNull(clientSecret, "PaymentIntent fixture should have a non-null clientSecret")

        // TokenManager is already mocked in setup()
        AirwallexPaymentSession.Builder(
            paymentIntent = paymentIntent,
            countryCode = "US"
        )

        verify(exactly = 1) {
            TokenManager.updateClientSecret(clientSecret)
        }
    }

    // same as above, this case will always be null for now
    @Ignore
    @Test
    fun `init block does not call TokenManager when clientSecret is null`() {
        val paymentIntentWithNullSecret = mockk<PaymentIntent> {
            every { clientSecret } returns null
            every { currency } returns "USD"
            every { amount } returns BigDecimal(100.0)
            every { customerId } returns null
        }

        AirwallexPaymentSession.Builder(
            paymentIntent = paymentIntentWithNullSecret,
            countryCode = "US"
        )

        // Should not crash and should not call updateClientSecret with null
        verify(exactly = 0) {
            TokenManager.updateClientSecret(any())
        }
    }

    @Test
    fun `build throws exception when both paymentIntent and paymentIntentProvider are null`() {
        // Use reflection to test the defensive require check in build()
        val builder = AirwallexPaymentSession.Builder(
            PaymentIntentFixtures.PAYMENT_INTENT,
            "CN"
        )

        // Use reflection to set both paymentIntent and paymentIntentProvider to null
        val builderClass = builder.javaClass
        val paymentIntentField = builderClass.getDeclaredField("paymentIntent")
        paymentIntentField.isAccessible = true
        paymentIntentField.set(builder, null)

        val paymentIntentProviderField = builderClass.getDeclaredField("paymentIntentProvider")
        paymentIntentProviderField.isAccessible = true
        paymentIntentProviderField.set(builder, null)

        // Now calling build() should throw IllegalArgumentException
        val exception = assertFailsWith<IllegalArgumentException> {
            builder.build()
        }
        assertEquals("Either paymentIntent or paymentIntentProvider must be provided", exception.message)
    }

    @Test
    fun `build normalizes empty customerId to null for PaymentIntent constructor`() {
        val paymentIntentWithEmptyCustomerId = mockk<PaymentIntent> {
            every { clientSecret } returns null
            every { currency } returns "USD"
            every { amount } returns BigDecimal(100.0)
            every { customerId } returns ""
        }

        val session = AirwallexPaymentSession.Builder(
            paymentIntent = paymentIntentWithEmptyCustomerId,
            countryCode = "US"
        ).build()

        assertNull(session.customerId)
    }

    @Test
    fun `build normalizes empty customerId to null for PaymentIntentProvider constructor`() {
        val testProvider = TestPaymentIntentProvider(
            currency = "USD",
            amount = BigDecimal(50.0)
        )

        val session = AirwallexPaymentSession.Builder(
            paymentIntentProvider = testProvider,
            countryCode = "US",
            customerId = ""
        ).build()

        assertNull(session.customerId)
    }

    @Test
    fun `build normalizes empty customerId to null for PaymentIntentSource constructor`() {
        val testSource = TestPaymentIntentSource(
            currency = "EUR",
            amount = BigDecimal(75.0)
        )

        val session = AirwallexPaymentSession.Builder(
            paymentIntentSource = testSource,
            countryCode = "DE",
            customerId = ""
        ).build()

        assertNull(session.customerId)
    }

    @Test
    fun `init block does not call TokenManager when PaymentIntent has null clientSecret`() {
        val paymentIntentWithNullSecret = mockk<PaymentIntent> {
            every { clientSecret } returns null
            every { currency } returns "USD"
            every { amount } returns BigDecimal(100.0)
            every { customerId } returns null
        }

        AirwallexPaymentSession.Builder(
            paymentIntent = paymentIntentWithNullSecret,
            countryCode = "US"
        )

        // Should not call updateClientSecret when clientSecret is null
        verify(exactly = 0) {
            TokenManager.updateClientSecret(any())
        }
    }

}
