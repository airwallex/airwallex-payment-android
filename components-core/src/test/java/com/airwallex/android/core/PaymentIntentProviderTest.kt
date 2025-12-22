package com.airwallex.android.core

import android.app.Activity
import android.app.Application
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PaymentIntentFixtures
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkObject
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PaymentIntentProviderTest {

    @Before
    fun resetRepository() {
        // Reset the repository state before each test using reflection
        val repositoryClass = PaymentIntentProviderRepository::class.java
        val isInitializedField = repositoryClass.getDeclaredField("isInitialized")
        isInitializedField.isAccessible = true
        isInitializedField.setBoolean(PaymentIntentProviderRepository, false)

        val providersField = repositoryClass.getDeclaredField("providers")
        providersField.isAccessible = true
        val providers =
            providersField.get(PaymentIntentProviderRepository) as java.util.concurrent.ConcurrentHashMap<*, *>
        providers.clear()

        val activityToProviderMapField = repositoryClass.getDeclaredField("activityToProviderMap")
        activityToProviderMapField.isAccessible = true
        val activityToProviderMap =
            activityToProviderMapField.get(PaymentIntentProviderRepository) as java.util.concurrent.ConcurrentHashMap<*, *>
        activityToProviderMap.clear()

        val providerToActivitiesMapField =
            repositoryClass.getDeclaredField("providerToActivitiesMap")
        providerToActivitiesMapField.isAccessible = true
        val providerToActivitiesMap =
            providerToActivitiesMapField.get(PaymentIntentProviderRepository) as java.util.concurrent.ConcurrentHashMap<*, *>
        providerToActivitiesMap.clear()
    }

    // Helper function to create a test callback
    private fun createTestCallback(): Triple<PaymentIntentProvider.PaymentIntentCallback, () -> PaymentIntent?, () -> Throwable?> {
        var capturedPaymentIntent: PaymentIntent? = null
        var capturedError: Throwable? = null

        val callback = object : PaymentIntentProvider.PaymentIntentCallback {
            override fun onSuccess(paymentIntent: PaymentIntent) {
                capturedPaymentIntent = paymentIntent
            }

            override fun onError(error: Throwable) {
                capturedError = error
            }
        }

        return Triple(callback, { capturedPaymentIntent }, { capturedError })
    }

    private fun createTestProvider(
        currency: String = "USD",
        amount: BigDecimal = BigDecimal(50.0)
    ) = TestPaymentIntentProvider(currency, amount)

    @Test
    fun `PaymentIntentProvider interfaces have correct properties`() {
        val testProvider = TestPaymentIntentProvider(
            currency = "USD",
            amount = BigDecimal(50.0)
        )

        assertEquals("USD", testProvider.currency)
        assertEquals(BigDecimal(50.0), testProvider.amount)

        val testSource = TestPaymentIntentSource(
            currency = "EUR",
            amount = BigDecimal(100.0)
        )

        assertEquals("EUR", testSource.currency)
        assertEquals(BigDecimal(100.0), testSource.amount)
    }

    @Test
    fun `SourceToProviderAdapter basic functionality`() = runTest {
        val testSource = TestPaymentIntentSource(
            currency = "EUR",
            amount = BigDecimal(100.0)
        )

        val adapter = SourceToProviderAdapter(testSource)
        assertEquals("EUR", adapter.currency)
        assertEquals(BigDecimal(100.0), adapter.amount)
    }

    @Test
    fun `PaymentIntentProvider callback interface works correctly`() {
        val testProvider = createTestProvider()
        val (callback, getIntent, getError) = createTestCallback()

        testProvider.provide(callback)

        assertEquals(PaymentIntentFixtures.PAYMENT_INTENT, getIntent())
        assertNull(getError())
    }

    private class TestPaymentIntentProvider(
        override val currency: String,
        override val amount: BigDecimal
    ) : PaymentIntentProvider {
        override fun provide(callback: PaymentIntentProvider.PaymentIntentCallback) {
            // Simulate async operation
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
    fun `SourceToProviderAdapter provide calls onSuccess when source succeeds`() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        val testSource = TestPaymentIntentSource(currency = "EUR", amount = BigDecimal(100.0))
        val adapter =
            SourceToProviderAdapter(source = testSource, scope = CoroutineScope(testDispatcher))
        val (callback, getIntent, getError) = createTestCallback()

        adapter.provide(callback)
        testScheduler.advanceUntilIdle()

        assertEquals(PaymentIntentFixtures.PAYMENT_INTENT, getIntent())
        assertNull(getError())
    }

    @Test
    fun `SourceToProviderAdapter provide calls onError when source throws exception`() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        val testException = RuntimeException("Test error")
        val testSource = object : PaymentIntentSource {
            override val currency: String = "USD"
            override val amount: BigDecimal = BigDecimal(50.0)
            override suspend fun getPaymentIntent(): PaymentIntent = throw testException
        }
        val adapter =
            SourceToProviderAdapter(source = testSource, scope = CoroutineScope(testDispatcher))
        val (callback, getIntent, getError) = createTestCallback()

        adapter.provide(callback)
        testScheduler.advanceUntilIdle()

        assertNull(getIntent())
        assertEquals(testException, getError())
    }

    @Test
    fun `PaymentIntentProviderRepository initialize registers lifecycle callbacks`() {
        val mockApplication = mockk<Application>(relaxed = true)

        PaymentIntentProviderRepository.initialize(mockApplication)

        verify { mockApplication.registerActivityLifecycleCallbacks(any()) }
    }

    @Test
    fun `PaymentIntentProviderRepository initialize can be called multiple times safely`() {
        val mockApplication = mockk<Application>(relaxed = true)

        PaymentIntentProviderRepository.initialize(mockApplication)
        PaymentIntentProviderRepository.initialize(mockApplication)

        // Should only register once even if called multiple times
        verify(exactly = 1) { mockApplication.registerActivityLifecycleCallbacks(any()) }
    }

    @Test
    fun `PaymentIntentProviderRepository cleans up providers when activity is destroyed and finishing`() {
        val mockApplication = mockk<Application>(relaxed = true)
        val callbackSlot = slot<Application.ActivityLifecycleCallbacks>()
        every { mockApplication.registerActivityLifecycleCallbacks(capture(callbackSlot)) } returns Unit

        PaymentIntentProviderRepository.initialize(mockApplication)
        val providerId = PaymentIntentProviderRepository.store(createTestProvider())
        val testActivity = TestActivity(isFinishing = true)

        PaymentIntentProviderRepository.bindToActivity(providerId, testActivity)
        assertNotNull(PaymentIntentProviderRepository.get(providerId))

        callbackSlot.captured.onActivityDestroyed(testActivity)
        assertNull(PaymentIntentProviderRepository.get(providerId))
    }

    @Test
    fun `PaymentIntentProviderRepository does not clean up providers on configuration change`() {
        val mockApplication = mockk<Application>(relaxed = true)
        val callbackSlot = slot<Application.ActivityLifecycleCallbacks>()
        every { mockApplication.registerActivityLifecycleCallbacks(capture(callbackSlot)) } returns Unit

        PaymentIntentProviderRepository.initialize(mockApplication)
        val providerId = PaymentIntentProviderRepository.store(createTestProvider())
        val testActivity = TestActivity(isFinishing = false)

        PaymentIntentProviderRepository.bindToActivity(providerId, testActivity)
        assertNotNull(PaymentIntentProviderRepository.get(providerId))

        callbackSlot.captured.onActivityDestroyed(testActivity)
        assertNotNull(PaymentIntentProviderRepository.get(providerId))
    }

    @Test
    fun `PaymentIntentProviderRepository bindToActivity binds provider to activity`() {
        val providerId = PaymentIntentProviderRepository.store(createTestProvider())
        val testActivity = TestActivity(isFinishing = false)

        PaymentIntentProviderRepository.bindToActivity(providerId, testActivity)

        assertNotNull(PaymentIntentProviderRepository.get(providerId))
    }

    @Test
    fun `PaymentIntentProviderRepository bindToActivity does nothing for non-existent provider`() {
        val testActivity = TestActivity(isFinishing = false)

        // Should not throw exception when binding non-existent provider
        PaymentIntentProviderRepository.bindToActivity("non-existent-id", testActivity)
    }

    @Test
    fun `AirwallexPaymentSession bindToActivity extension function binds provider`() {
        val providerId = PaymentIntentProviderRepository.store(createTestProvider())
        val session = mockk<AirwallexPaymentSession>(relaxed = true)
        every { session.paymentIntentProviderId } returns providerId

        session.bindToActivity(TestActivity(isFinishing = false))

        assertNotNull(PaymentIntentProviderRepository.get(providerId))
    }

    @Test
    fun `AirwallexPaymentSession resolvePaymentIntent returns intent when available`() {
        val paymentIntent = PaymentIntentFixtures.PAYMENT_INTENT
        val session = mockk<AirwallexPaymentSession>(relaxed = true)
        every { session.paymentIntent } returns paymentIntent
        every { session.paymentIntentProviderId } returns null
        val (callback, getIntent, getError) = createTestCallback()

        session.resolvePaymentIntent(callback)

        assertEquals(paymentIntent, getIntent())
        assertNull(getError())
    }

    @Test
    fun `AirwallexPaymentSession resolvePaymentIntent uses provider when intent not available`() {
        val testProvider = createTestProvider()
        val session = mockk<AirwallexPaymentSession>(relaxed = true)
        every { session.paymentIntent } returns null
        every { session.paymentIntentProvider } returns testProvider
        every { session.paymentIntentProviderId } returns null
        val (callback, getIntent, getError) = createTestCallback()

        session.resolvePaymentIntent(callback)

        assertNotNull(getIntent())
        assertEquals(PaymentIntentFixtures.PAYMENT_INTENT, getIntent())
        assertNull(getError())
    }

    @Test
    fun `AirwallexPaymentSession resolvePaymentIntent returns error when provider fails`() {
        val testException = RuntimeException("Provider error")
        val errorProvider = object : PaymentIntentProvider {
            override val currency: String = "USD"
            override val amount: BigDecimal = BigDecimal(50.0)
            override fun provide(callback: PaymentIntentProvider.PaymentIntentCallback) {
                callback.onError(testException)
            }
        }
        val session = mockk<AirwallexPaymentSession>(relaxed = true)
        every { session.paymentIntent } returns null
        every { session.paymentIntentProvider } returns errorProvider
        every { session.paymentIntentProviderId } returns null
        val (callback, getIntent, getError) = createTestCallback()

        session.resolvePaymentIntent(callback)

        assertNull(getIntent())
        assertEquals(testException, getError())
    }

    @Test
    fun `AirwallexPaymentSession resolvePaymentIntent returns error when provider not found`() {
        val session = mockk<AirwallexPaymentSession>(relaxed = true)
        every { session.paymentIntent } returns null
        every { session.paymentIntentProvider } returns null
        every { session.paymentIntentProviderId } returns "non-existent-id"
        val (callback, getIntent, getError) = createTestCallback()

        session.resolvePaymentIntent(callback)

        assertNull(getIntent())
        assertIs<IllegalStateException>(getError()).apply {
            assertEquals(message?.contains("PaymentIntentProvider not found"), true)
        }
    }

    @Test
    fun `AirwallexPaymentSession resolvePaymentIntent returns error when neither intent nor provider available`() {
        val session = mockk<AirwallexPaymentSession>(relaxed = true)
        every { session.paymentIntent } returns null
        every { session.paymentIntentProvider } returns null
        every { session.paymentIntentProviderId } returns null
        val (callback, getIntent, getError) = createTestCallback()

        session.resolvePaymentIntent(callback)

        assertNull(getIntent())
        assertIs<IllegalStateException>(getError()).apply {
            assertEquals(
                message?.contains("Neither paymentIntent nor paymentIntentProvider available"),
                true
            )
        }
    }

    @Test
    fun `AirwallexRecurringWithIntentSession bindToActivity extension function binds provider`() {
        val providerId = PaymentIntentProviderRepository.store(createTestProvider())
        val session = mockk<AirwallexRecurringWithIntentSession>(relaxed = true)
        every { session.paymentIntentProviderId } returns providerId

        session.bindToActivity(TestActivity(isFinishing = false))

        assertNotNull(PaymentIntentProviderRepository.get(providerId))
    }

    @Test
    fun `AirwallexRecurringWithIntentSession resolvePaymentIntent returns intent when available`() {
        val paymentIntent = PaymentIntentFixtures.PAYMENT_INTENT
        val session = mockk<AirwallexRecurringWithIntentSession>(relaxed = true)
        every { session.paymentIntent } returns paymentIntent
        every { session.paymentIntentProviderId } returns null
        val (callback, getIntent, getError) = createTestCallback()

        session.resolvePaymentIntent(callback)

        assertEquals(paymentIntent, getIntent())
        assertNull(getError())
    }

    @Test
    fun `AirwallexRecurringWithIntentSession resolvePaymentIntent uses provider when intent not available`() {
        val testProvider = createTestProvider()
        val session = mockk<AirwallexRecurringWithIntentSession>(relaxed = true)
        every { session.paymentIntent } returns null
        every { session.paymentIntentProvider } returns testProvider
        every { session.paymentIntentProviderId } returns null
        val (callback, getIntent, getError) = createTestCallback()

        session.resolvePaymentIntent(callback)

        assertEquals(PaymentIntentFixtures.PAYMENT_INTENT, getIntent())
        assertNull(getError())
    }

    @Test
    fun `AirwallexRecurringWithIntentSession resolvePaymentIntent returns error when provider fails`() {
        val testException = RuntimeException("Provider error")
        val errorProvider = object : PaymentIntentProvider {
            override val currency: String = "USD"
            override val amount: BigDecimal = BigDecimal(50.0)
            override fun provide(callback: PaymentIntentProvider.PaymentIntentCallback) {
                callback.onError(testException)
            }
        }
        val session = mockk<AirwallexRecurringWithIntentSession>(relaxed = true)
        every { session.paymentIntent } returns null
        every { session.paymentIntentProvider } returns errorProvider
        every { session.paymentIntentProviderId } returns null
        val (callback, getIntent, getError) = createTestCallback()

        session.resolvePaymentIntent(callback)

        assertNull(getIntent())
        assertEquals(testException, getError())
    }

    @Test
    fun `AirwallexRecurringWithIntentSession resolvePaymentIntent returns error when provider not found`() {
        val session = mockk<AirwallexRecurringWithIntentSession>(relaxed = true)
        every { session.paymentIntent } returns null
        every { session.paymentIntentProvider } returns null
        every { session.paymentIntentProviderId } returns "non-existent-id"
        val (callback, getIntent, getError) = createTestCallback()

        session.resolvePaymentIntent(callback)

        assertNull(getIntent())
        assertIs<IllegalStateException>(getError()).apply {
            assertEquals(message?.contains("PaymentIntentProvider not found"), true)
        }
    }

    @Test
    fun `AirwallexRecurringWithIntentSession resolvePaymentIntent returns error when neither intent nor provider available`() {
        val session = mockk<AirwallexRecurringWithIntentSession>(relaxed = true)
        every { session.paymentIntent } returns null
        every { session.paymentIntentProvider } returns null
        every { session.paymentIntentProviderId } returns null
        val (callback, getIntent, getError) = createTestCallback()

        session.resolvePaymentIntent(callback)

        assertNull(getIntent())
        assertIs<IllegalStateException>(getError()).apply {
            assertEquals(
                message?.contains("Neither paymentIntent nor paymentIntentProvider available"),
                true
            )
        }
    }

    @Test
    fun `PaymentIntentCallback onSuccess is called correctly`() {
        val testIntent = PaymentIntentFixtures.PAYMENT_INTENT
        val (callback, getIntent, getError) = createTestCallback()

        callback.onSuccess(testIntent)

        assertEquals(testIntent, getIntent())
        assertNull(getError())
    }

    @Test
    fun `PaymentIntentCallback onError is called correctly`() {
        val testException = RuntimeException("Test error")
        val (callback, getIntent, getError) = createTestCallback()

        callback.onError(testException)

        assertNull(getIntent())
        assertEquals(testException, getError())
    }

    @Test
    fun `AirwallexSession bindToActivity handles AirwallexPaymentSession`() {
        val providerId = PaymentIntentProviderRepository.store(createTestProvider())
        val session: AirwallexSession = mockk<AirwallexPaymentSession>(relaxed = true)
        every { (session as AirwallexPaymentSession).paymentIntentProviderId } returns providerId

        session.bindToActivity(TestActivity(isFinishing = false))

        assertNotNull(PaymentIntentProviderRepository.get(providerId))
    }

    @Test
    fun `AirwallexSession bindToActivity handles AirwallexRecurringWithIntentSession`() {
        val providerId = PaymentIntentProviderRepository.store(createTestProvider())
        val session: AirwallexSession = mockk<AirwallexRecurringWithIntentSession>(relaxed = true)
        every { (session as AirwallexRecurringWithIntentSession).paymentIntentProviderId } returns providerId

        session.bindToActivity(TestActivity(isFinishing = false))

        assertNotNull(PaymentIntentProviderRepository.get(providerId))
    }

    @Test
    fun `AirwallexPaymentSession resolvePaymentIntent uses providerId from repository and updates TokenManager`() {
        mockkObject(TokenManager)
        try {
            val testProvider = createTestProvider()
            val providerId = PaymentIntentProviderRepository.store(testProvider)
            val session = mockk<AirwallexPaymentSession>(relaxed = true)
            every { session.paymentIntent } returns null
            every { session.paymentIntentProvider } returns null
            every { session.paymentIntentProviderId } returns providerId
            val (callback, getIntent, getError) = createTestCallback()

            session.resolvePaymentIntent(callback)

            assertEquals(PaymentIntentFixtures.PAYMENT_INTENT, getIntent())
            assertNull(getError())
            verify { TokenManager.updateClientSecret(requireNotNull(PaymentIntentFixtures.PAYMENT_INTENT.clientSecret)) }
        } finally {
            unmockkObject(TokenManager)
        }
    }

    @Test
    fun `PaymentIntentProviderRepository bindToActivity rebinds to new provider and cleans up old provider`() {
        val oldProvider = createTestProvider()
        val newProvider = createTestProvider()
        val oldProviderId = PaymentIntentProviderRepository.store(oldProvider)
        val newProviderId = PaymentIntentProviderRepository.store(newProvider)
        val testActivity = TestActivity(isFinishing = false)

        // Bind to old provider first
        PaymentIntentProviderRepository.bindToActivity(oldProviderId, testActivity)
        assertNotNull(PaymentIntentProviderRepository.get(oldProviderId))
        assertNotNull(PaymentIntentProviderRepository.get(newProviderId))

        // Rebind to new provider
        PaymentIntentProviderRepository.bindToActivity(newProviderId, testActivity)

        // Old provider should be cleaned up, new provider should still exist
        assertNull(PaymentIntentProviderRepository.get(oldProviderId))
        assertNotNull(PaymentIntentProviderRepository.get(newProviderId))
    }

    @Test
    fun `AirwallexRecurringWithIntentSession resolvePaymentIntent uses providerId from repository and updates TokenManager`() {
        mockkObject(TokenManager)
        try {
            val testProvider = createTestProvider()
            val providerId = PaymentIntentProviderRepository.store(testProvider)
            val session = mockk<AirwallexRecurringWithIntentSession>(relaxed = true)
            every { session.paymentIntent } returns null
            every { session.paymentIntentProvider } returns null
            every { session.paymentIntentProviderId } returns providerId
            val (callback, getIntent, getError) = createTestCallback()

            session.resolvePaymentIntent(callback)

            assertEquals(PaymentIntentFixtures.PAYMENT_INTENT, getIntent())
            assertNull(getError())
            verify { TokenManager.updateClientSecret(requireNotNull(PaymentIntentFixtures.PAYMENT_INTENT.clientSecret)) }
        } finally {
            unmockkObject(TokenManager)
        }
    }

    @Test
    fun `AirwallexPaymentSession bindToActivity stores provider when providerId is null`() {
        val testProvider = createTestProvider()
        val session = mockk<AirwallexPaymentSession>(relaxed = true)
        val testActivity = TestActivity(isFinishing = false)
        var capturedProviderId: String? = null

        every { session.paymentIntent } returns null
        every { session.paymentIntentProvider } returns testProvider
        every { session.paymentIntentProviderId } returns null
        every { session.paymentIntentProviderId = any() } answers {
            capturedProviderId = firstArg()
        }

        session.bindToActivity(testActivity)

        // Verify that a provider ID was set on the session
        assertNotNull(capturedProviderId)
        capturedProviderId?.let { providerId ->
            // Verify that the provider was stored in the repository
            assertNotNull(PaymentIntentProviderRepository.get(providerId))
            // Verify it's the same provider
            assertEquals(testProvider, PaymentIntentProviderRepository.get(providerId))
        }
    }

    @Test
    fun `AirwallexRecurringWithIntentSession bindToActivity stores provider when providerId is null`() {
        val testProvider = createTestProvider()
        val session = mockk<AirwallexRecurringWithIntentSession>(relaxed = true)
        val testActivity = TestActivity(isFinishing = false)
        var capturedProviderId: String? = null

        every { session.paymentIntent } returns null
        every { session.paymentIntentProvider } returns testProvider
        every { session.paymentIntentProviderId } returns null
        every { session.paymentIntentProviderId = any() } answers {
            capturedProviderId = firstArg()
        }

        session.bindToActivity(testActivity)

        // Verify that a provider ID was set on the session
        assertNotNull(capturedProviderId)
        capturedProviderId?.let { providerId ->
            // Verify that the provider was stored in the repository
            assertNotNull(PaymentIntentProviderRepository.get(providerId))
            // Verify it's the same provider
            assertEquals(testProvider, PaymentIntentProviderRepository.get(providerId))
        }
    }

    // Test helper classes
    private class TestActivity(
        private val isFinishing: Boolean
    ) : Activity() {
        override fun isFinishing(): Boolean = isFinishing
    }
}