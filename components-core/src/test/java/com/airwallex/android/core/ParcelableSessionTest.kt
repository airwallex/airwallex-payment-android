package com.airwallex.android.core

import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PaymentIntentFixtures
import com.airwallex.android.core.model.Shipping
import com.airwallex.android.core.model.ShippingFixtures
import io.mockk.mockk
import org.junit.After
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ParcelableSessionTest {

    private val testPaymentIntent: PaymentIntent = PaymentIntentFixtures.PAYMENT_INTENT
    private val testShipping: Shipping = ShippingFixtures.SHIPPING

    private fun makeParcelableSession(
        paymentIntent: PaymentIntent? = testPaymentIntent,
        paymentIntentProviderId: String? = null,
        requiredBillingContactFields: Set<RequiredBillingContactField>? = null,
        shipping: Shipping? = testShipping,
    ) = ParcelableSession(
        paymentIntent = paymentIntent,
        paymentIntentProviderId = paymentIntentProviderId,
        paymentConsentOptions = null,
        currency = "USD",
        countryCode = "US",
        amount = BigDecimal("12.34"),
        shipping = shipping,
        isBillingInformationRequired = true,
        isEmailRequired = true,
        customerId = "cus_123",
        returnUrl = "https://example.com/return",
        googlePayOptions = null,
        paymentMethods = listOf("card", "googlepay"),
        autoCapture = true,
        hidePaymentConsents = false,
        requiredBillingContactFields = requiredBillingContactFields,
    )

    @After
    fun resetProviderRepository() {
        // PaymentIntentProviderRepository is a process-wide singleton; clear anything
        // a test attached so subsequent tests start from a clean state.
        clearProviderRepository()
    }

    @Test
    fun `toSession maps every scalar field through`() {
        val explicitFields = setOf(
            RequiredBillingContactField.NAME,
            RequiredBillingContactField.EMAIL,
            RequiredBillingContactField.ADDRESS,
        )
        val parcelable = makeParcelableSession(
            requiredBillingContactFields = explicitFields,
        )

        val session = parcelable.toSession()

        assertSame(testPaymentIntent, session.paymentIntent)
        assertEquals("USD", session.currency)
        assertEquals("US", session.countryCode)
        assertEquals(BigDecimal("12.34"), session.amount)
        assertSame(testShipping, session.shipping)
        @Suppress("DEPRECATION")
        assertTrue(session.isBillingInformationRequired)
        @Suppress("DEPRECATION")
        assertTrue(session.isEmailRequired)
        assertEquals("cus_123", session.customerId)
        assertEquals("https://example.com/return", session.returnUrl)
        assertEquals(listOf("card", "googlepay"), session.paymentMethods)
        assertTrue(session.autoCapture)
        assertEquals(explicitFields, session.requiredBillingContactFields)
    }

    @Test
    fun `toSession re-attaches provider stored in the repository`() {
        val provider = mockk<PaymentIntentProvider>()
        val providerId = PaymentIntentProviderRepository.store(provider)

        val parcelable = makeParcelableSession(paymentIntentProviderId = providerId)
        val session = parcelable.toSession()

        assertSame(provider, session.paymentIntentProvider)
        assertEquals(providerId, session.paymentIntentProviderId)
    }

    @Test
    fun `toSession leaves provider null when providerId is null`() {
        val session = makeParcelableSession(paymentIntentProviderId = null).toSession()

        assertNull(session.paymentIntentProvider)
        assertNull(session.paymentIntentProviderId)
    }

    @Test
    fun `toSession leaves provider null when providerId is not in repository`() {
        // Provider was cleaned up (e.g. host Activity finished) but the parcel still
        // carries the stale id — toSession() must not throw or fabricate a provider.
        val session = makeParcelableSession(paymentIntentProviderId = "never-stored")
            .toSession()

        assertNull(session.paymentIntentProvider)
        assertEquals("never-stored", session.paymentIntentProviderId)
    }

    private fun clearProviderRepository() {
        // The repository is `internal object` with no clear()/remove() API. Reset via
        // reflection so tests that store a provider don't leak it into other tests.
        val providersField = PaymentIntentProviderRepository::class.java
            .getDeclaredField("providers")
        providersField.isAccessible = true
        (providersField.get(PaymentIntentProviderRepository) as MutableMap<*, *>).clear()

        val activityToProviderField = PaymentIntentProviderRepository::class.java
            .getDeclaredField("activityToProviderMap")
        activityToProviderField.isAccessible = true
        (activityToProviderField.get(PaymentIntentProviderRepository) as MutableMap<*, *>).clear()

        val providerToActivitiesField = PaymentIntentProviderRepository::class.java
            .getDeclaredField("providerToActivitiesMap")
        providerToActivitiesField.isAccessible = true
        (providerToActivitiesField.get(PaymentIntentProviderRepository) as MutableMap<*, *>).clear()
    }
}
