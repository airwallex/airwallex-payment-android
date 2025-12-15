package com.airwallex.android.core

import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PaymentIntentFixtures
import com.airwallex.android.core.model.Shipping
import io.mockk.every
import io.mockk.just
import io.mockk.mockkObject
import io.mockk.runs
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
}
