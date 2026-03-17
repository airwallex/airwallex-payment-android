package com.airwallex.android.core

import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PaymentIntentFixtures
import com.airwallex.android.core.model.Shipping
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AirwallexRecurringWithIntentSessionTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkObject(TokenManager)
        every { TokenManager.updateClientSecret(any()) } just runs
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun buildTest() {
        val shipping = Shipping.Builder()
            .setFirstName("John")
            .setLastName("Doe")
            .build()
        val googlePayOptions = GooglePayOptions(
            billingAddressRequired = true,
            billingAddressParameters = BillingAddressParameters(BillingAddressParameters.Format.FULL),
        )
        val airwallexRecurringWithIntentSession = AirwallexRecurringWithIntentSession.Builder(
            nextTriggerBy = PaymentConsent.NextTriggeredBy.CUSTOMER,
            customerId = "cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu",
            paymentIntent = PaymentIntentFixtures.PAYMENT_INTENT,
            countryCode = "CN"
        )
            .setMerchantTriggerReason(PaymentConsent.MerchantTriggerReason.SCHEDULED)
            .setRequireBillingInformation(false)
            .setRequireCvc(true)
            .setRequireEmail(true)
            .setReturnUrl("airwallexcheckout://com.airwallex.paymentacceptance")
            .setAutoCapture(false)
            .setGooglePayOptions(googlePayOptions)
            .setPaymentMethods(listOf("googlepay"))
            .setShipping(shipping)
            .build()

        assertNotNull(airwallexRecurringWithIntentSession)

        assertEquals("AUD", airwallexRecurringWithIntentSession.currency)

        assertEquals(BigDecimal.valueOf(100.01), airwallexRecurringWithIntentSession.amount)

        assertNotNull(airwallexRecurringWithIntentSession.nextTriggerBy)
        assertEquals(
            PaymentConsent.NextTriggeredBy.CUSTOMER,
            airwallexRecurringWithIntentSession.nextTriggerBy
        )

        assertEquals(true, airwallexRecurringWithIntentSession.requiresCVC)
        assertNotNull(airwallexRecurringWithIntentSession.isBillingInformationRequired)
        assertEquals(false, airwallexRecurringWithIntentSession.isBillingInformationRequired)

        assertTrue(airwallexRecurringWithIntentSession.isEmailRequired)
        assertNotNull(airwallexRecurringWithIntentSession.customerId)
        assertEquals(
            "cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu",
            airwallexRecurringWithIntentSession.customerId
        )

        assertEquals(
            true,
            airwallexRecurringWithIntentSession.googlePayOptions?.billingAddressRequired
        )
        assertEquals(
            BillingAddressParameters.Format.FULL,
            airwallexRecurringWithIntentSession.googlePayOptions?.billingAddressParameters?.format
        )

        assertEquals(
            "airwallexcheckout://com.airwallex.paymentacceptance",
            airwallexRecurringWithIntentSession.returnUrl
        )
        assertEquals(false, airwallexRecurringWithIntentSession.autoCapture)
        assertNotNull(airwallexRecurringWithIntentSession.paymentMethods)
        assertEquals(airwallexRecurringWithIntentSession.shipping, shipping)
    }

    @Test
    fun `build with PaymentIntentProvider`() {
        val testProvider = TestPaymentIntentProvider(
            currency = "USD",
            amount = BigDecimal(50.0)
        )

        val session = AirwallexRecurringWithIntentSession.Builder(
            paymentIntentProvider = testProvider,
            customerId = "test_customer",
            nextTriggerBy = PaymentConsent.NextTriggeredBy.CUSTOMER,
            countryCode = "US"
        ).build()

        assertNotNull(session)
        assertEquals("USD", session.currency)
        assertEquals(BigDecimal(50.0), session.amount)
        assertEquals("test_customer", session.customerId)
        assertEquals(PaymentConsent.NextTriggeredBy.CUSTOMER, session.nextTriggerBy)
        assertNotNull(session.paymentIntentProvider)
    }

    @Test
    fun `build with PaymentIntentSource`() {
        val testSource = TestPaymentIntentSource(
            currency = "EUR",
            amount = BigDecimal(75.0)
        )

        val session = AirwallexRecurringWithIntentSession.Builder(
            paymentIntentSource = testSource,
            customerId = "test_customer_de",
            nextTriggerBy = PaymentConsent.NextTriggeredBy.MERCHANT,
            countryCode = "DE"
        ).build()

        assertNotNull(session)
        assertEquals("EUR", session.currency)
        assertEquals(BigDecimal(75.0), session.amount)
        assertEquals("test_customer_de", session.customerId)
        assertEquals(PaymentConsent.NextTriggeredBy.MERCHANT, session.nextTriggerBy)
        assertNotNull(session.paymentIntentProvider)
    }

    @Test
    fun `build throws exception when both paymentIntent and paymentIntentProvider are null`() {
        // Use reflection to test the defensive require check in build()
        val builder = AirwallexRecurringWithIntentSession.Builder(
            paymentIntent = PaymentIntentFixtures.PAYMENT_INTENT,
            customerId = "test_customer",
            nextTriggerBy = PaymentConsent.NextTriggeredBy.CUSTOMER,
            countryCode = "US"
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
        val exception = kotlin.test.assertFailsWith<IllegalArgumentException> {
            builder.build()
        }
        assertEquals("Either paymentIntent or paymentIntentProvider must be provided", exception.message)
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
    fun `init block does not call TokenManager when PaymentIntent has null clientSecret`() {
        val paymentIntentWithNullSecret = mockk<PaymentIntent> {
            every { clientSecret } returns null
            every { currency } returns "USD"
            every { amount } returns BigDecimal(100.0)
        }

        AirwallexRecurringWithIntentSession.Builder(
            paymentIntent = paymentIntentWithNullSecret,
            customerId = "test_customer",
            nextTriggerBy = PaymentConsent.NextTriggeredBy.CUSTOMER,
            countryCode = "US"
        )

        // Should not call updateClientSecret when clientSecret is null
        verify(exactly = 0) {
            TokenManager.updateClientSecret(any())
        }
    }
}
