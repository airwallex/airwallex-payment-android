package com.airwallex.android.view

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexRecurringSession
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.Session
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.Billing
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PaymentIntentStatus
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.view.Constants.createPaymentMethod
import com.airwallex.android.view.PaymentFlowViewModel.PaymentFlowType
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

@Suppress("LargeClass")
class PaymentFlowViewModelTest {
    private lateinit var airwallex: Airwallex
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Initialize mocks
        mockkObject(AnalyticsLogger)

        // Create Airwallex mock with default behaviors
        airwallex = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.scheduler.cancel()

        // Clear all mocks and reset state
        clearMocks(airwallex)
        unmockkAll()
    }

    // ========== Helper Methods ==========

    private fun <T> TestScope.collectSharedFlow(
        flow: SharedFlow<T>,
        collector: MutableList<T> = mutableListOf()
    ): Pair<MutableList<T>, Job> {
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            flow.collect { collector.add(it) }
        }
        return Pair(collector, job)
    }

    private fun createMockPaymentConsent(
        id: String = "test_consent_id",
        paymentMethod: PaymentMethod? = createPaymentMethod("card"),
        fingerprint: String? = "default_fingerprint",
        nextTriggeredBy: PaymentConsent.NextTriggeredBy = PaymentConsent.NextTriggeredBy.CUSTOMER
    ): PaymentConsent {
        return mockk {
            every { this@mockk.id } returns id
            every { this@mockk.paymentMethod } returns paymentMethod?.let {
                mockk {
                    every { card } returns mockk {
                        every { this@mockk.fingerprint } returns fingerprint
                    }
                }
            }
            every { this@mockk.nextTriggeredBy } returns nextTriggeredBy
        }
    }

    private fun createViewModel(session: AirwallexSession): PaymentFlowViewModel {
        return PaymentFlowViewModel(airwallex, session)
    }

    // ========== Initial State Tests ==========

    @Test
    fun `test initial state of StateFlows`() = runTest {
        val session = createPaymentSession()
        val viewModel = createViewModel(session)

        // Verify initial states
        assertTrue(viewModel.availablePaymentMethods.value.isEmpty())
        assertTrue(viewModel.availablePaymentConsents.value.isEmpty())
    }

    // ========== Fetch Payment Methods and Consents Tests ==========

    @Test
    fun `test fetchAvailablePaymentMethodsAndConsents success`() = runTest {
        val session = createPaymentSession()
        val mockMethods = listOf(
            mockk<AvailablePaymentMethodType> {
                every { name } returns "card"
            }
        )
        val mockConsents = listOf(
            mockk<PaymentConsent> {
                every { id } returns "consent_1"
                every { paymentMethod } returns mockk {
                    every { card } returns mockk {
                        every { fingerprint } returns "test_fp_1"
                    }
                }
                every { nextTriggeredBy } returns PaymentConsent.NextTriggeredBy.CUSTOMER
            }
        )

        coEvery {
            airwallex.fetchAvailablePaymentMethodsAndConsents(session)
        } returns Result.success(Pair(mockMethods, mockConsents))

        val viewModel = createViewModel(session)
        val result = viewModel.fetchAvailablePaymentMethodsAndConsents()

        assertTrue(result.isSuccess)
        assertEquals(mockMethods, result.getOrNull()?.first)
        assertEquals(mockConsents, result.getOrNull()?.second)
        assertEquals(mockMethods, viewModel.availablePaymentMethods.value)
        assertEquals(mockConsents, viewModel.availablePaymentConsents.value)
    }

    @Test
    fun `test fetchAvailablePaymentMethodsAndConsents failure`() = runTest {
        val session = createPaymentSession()
        val exception = AirwallexCheckoutException(message = "Test error")

        coEvery {
            airwallex.fetchAvailablePaymentMethodsAndConsents(session)
        } returns Result.failure(exception)

        val viewModel = createViewModel(session)
        val result = viewModel.fetchAvailablePaymentMethodsAndConsents()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AirwallexCheckoutException)
        // StateFlows should remain empty on failure
        assertTrue(viewModel.availablePaymentMethods.value.isEmpty())
        assertTrue(viewModel.availablePaymentConsents.value.isEmpty())
    }

    @Test
    fun `test fetchAvailablePaymentMethodsAndConsents with empty results`() = runTest {
        val session = createPaymentSession()

        coEvery {
            airwallex.fetchAvailablePaymentMethodsAndConsents(session)
        } returns Result.success(Pair(emptyList(), emptyList()))

        val viewModel = createViewModel(session)
        val result = viewModel.fetchAvailablePaymentMethodsAndConsents()

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.first?.isEmpty() == true)
        assertTrue(result.getOrNull()?.second?.isEmpty() == true)
        assertTrue(viewModel.availablePaymentMethods.value.isEmpty())
        assertTrue(viewModel.availablePaymentConsents.value.isEmpty())
    }

    // ========== Consent Deduplication Tests ==========
    @Suppress("LongMethod")
    @Test
    fun `test consent deduplication - CIT takes priority over MIT, then MIT appears after CIT deletion`() = runTest {
        val session = createPaymentSession()

        // Create consents with various scenarios
        val consentWithNullFingerprint = mockk<PaymentConsent> {
            every { id } returns "consent_null_fp"
            every { paymentMethod } returns mockk {
                every { card } returns mockk {
                    every { fingerprint } returns null
                }
            }
            every { nextTriggeredBy } returns PaymentConsent.NextTriggeredBy.CUSTOMER
        }

        val citConsent = mockk<PaymentConsent> {
            every { id } returns "consent_cit"
            every { paymentMethod } returns mockk {
                every { card } returns mockk {
                    every { fingerprint } returns "fp1"
                }
            }
            every { nextTriggeredBy } returns PaymentConsent.NextTriggeredBy.CUSTOMER
        }

        val mitConsent1 = mockk<PaymentConsent> {
            every { id } returns "consent_mit_1"
            every { paymentMethod } returns mockk {
                every { card } returns mockk {
                    every { fingerprint } returns "fp1"
                }
            }
            every { nextTriggeredBy } returns PaymentConsent.NextTriggeredBy.MERCHANT
        }

        val mitConsent2 = mockk<PaymentConsent> {
            every { id } returns "consent_mit_2"
            every { paymentMethod } returns mockk {
                every { card } returns mockk {
                    every { fingerprint } returns "fp1"
                }
            }
            every { nextTriggeredBy } returns PaymentConsent.NextTriggeredBy.MERCHANT
        }

        val allConsents = listOf(consentWithNullFingerprint, citConsent, mitConsent1, mitConsent2)
        val mockMethods = listOf(mockk<AvailablePaymentMethodType> { every { name } returns "card" })

        coEvery {
            airwallex.fetchAvailablePaymentMethodsAndConsents(session)
        } returns Result.success(Pair(mockMethods, allConsents))

        val viewModel = createViewModel(session)
        viewModel.fetchAvailablePaymentMethodsAndConsents()
        advanceUntilIdle()

        // Assert: Should show null fingerprint consent and CIT consent only (MIT consents are hidden)
        assertEquals(2, viewModel.availablePaymentConsents.value.size)
        assertTrue(viewModel.availablePaymentConsents.value.contains(consentWithNullFingerprint))
        assertTrue(viewModel.availablePaymentConsents.value.contains(citConsent))

        // Now delete the CIT consent
        val slot = slot<Airwallex.PaymentListener<PaymentConsent>>()
        coEvery { airwallex.getClientSecret(session) } returns "test_client_secret"
        coEvery {
            airwallex.disablePaymentConsent(any(), capture(slot))
        } answers {
            slot.captured.onSuccess(citConsent)
        }

        viewModel.deletePaymentConsent(citConsent)
        advanceUntilIdle()

        // Assert: After CIT deletion, should show null fingerprint consent and first MIT consent
        assertEquals(2, viewModel.availablePaymentConsents.value.size)
        assertTrue(viewModel.availablePaymentConsents.value.contains(consentWithNullFingerprint))
        assertTrue(viewModel.availablePaymentConsents.value.contains(mitConsent1))
        // mitConsent2 should still be filtered out (only first MIT is kept)
        assertTrue(!viewModel.availablePaymentConsents.value.contains(mitConsent2))
    }

    // ========== Delete Payment Consent Tests ==========

    @Test
    fun `test deletePaymentConsent success`() = runTest {
        val session = createPaymentSession()
        val paymentConsent = createMockPaymentConsent(id = "consent_id")

        val slot = slot<Airwallex.PaymentListener<PaymentConsent>>()
        coEvery {
            airwallex.getClientSecret(session)
        } returns "test_client_secret"

        coEvery {
            airwallex.disablePaymentConsent(any(), capture(slot))
        } answers {
            slot.captured.onSuccess(paymentConsent)
        }

        val viewModel = createViewModel(session)
        val (results, job) = collectSharedFlow(viewModel.deleteConsentResult)

        viewModel.deletePaymentConsent(paymentConsent)
        advanceUntilIdle()

        assertEquals(1, results.size)
        val result = results.first()
        assertTrue(result is PaymentFlowViewModel.DeleteConsentResult.Success)
        assertEquals(paymentConsent, (result as PaymentFlowViewModel.DeleteConsentResult.Success).consent)

        coVerify {
            airwallex.disablePaymentConsent(
                match { it.paymentConsentId == "consent_id" },
                any()
            )
        }

        job.cancel()
    }

    @Test
    fun `test deletePaymentConsent failure when clientSecret is null`() = runTest {
        val session = createPaymentSession()
        val paymentConsent = createMockPaymentConsent(id = "consent_id")

        coEvery {
            airwallex.getClientSecret(session)
        } returns null

        val viewModel = createViewModel(session)
        val (results, job) = collectSharedFlow(viewModel.deleteConsentResult)

        viewModel.deletePaymentConsent(paymentConsent)
        advanceUntilIdle()

        assertEquals(1, results.size)
        val result = results.first()
        assertTrue(result is PaymentFlowViewModel.DeleteConsentResult.Failure)
        val failureResult = result as PaymentFlowViewModel.DeleteConsentResult.Failure
        assertNotNull(failureResult.exception)
        assertTrue(failureResult.exception is AirwallexCheckoutException)
        assertEquals("clientSecret is null", failureResult.exception.message)

        job.cancel()
    }

    @Test
    fun `test deletePaymentConsent calls onFailed on exception`() = runTest {
        val session = createPaymentSession()
        val paymentConsent = createMockPaymentConsent(id = "test_consent_id")

        val exception = AirwallexCheckoutException(message = "Test exception")
        val slot = slot<Airwallex.PaymentListener<PaymentConsent>>()

        coEvery {
            airwallex.getClientSecret(session)
        } returns "test_client_secret"

        coEvery {
            airwallex.disablePaymentConsent(any(), capture(slot))
        } answers {
            slot.captured.onFailed(exception)
        }

        val viewModel = createViewModel(session)
        val (results, job) = collectSharedFlow(viewModel.deleteConsentResult)

        viewModel.deletePaymentConsent(paymentConsent)
        advanceUntilIdle()

        assertEquals(1, results.size)
        val result = results.first()
        assertTrue(result is PaymentFlowViewModel.DeleteConsentResult.Failure)
        val failureResult = result as PaymentFlowViewModel.DeleteConsentResult.Failure
        assertNotNull(failureResult.exception)
        assertTrue(failureResult.exception is AirwallexCheckoutException)
        assertEquals("Test exception", failureResult.exception.message)

        job.cancel()
    }

    // ========== Confirm Payment Intent Tests ==========

    @Test
    fun `test confirmPaymentIntent with invalid session`() = runTest {
        val session = createRecurringSession()
        val paymentConsent = mockk<PaymentConsent>(relaxed = true)
        val error = AirwallexCheckoutException(message = "confirm with paymentConsent only support AirwallexPaymentSession")
        val expectedStatus = AirwallexPaymentStatus.Failure(error)

        val slot = slot<Airwallex.PaymentResultListener>()
        coEvery {
            airwallex.confirmPaymentIntent(
                session = session,
                paymentConsent = paymentConsent,
                listener = capture(slot)
            )
        } answers {
            slot.captured.onCompleted(expectedStatus)
        }

        val viewModel = createViewModel(session)
        val (results, job) = collectSharedFlow(viewModel.paymentResult)

        viewModel.confirmPaymentIntent(paymentConsent)
        advanceUntilIdle()

        assertEquals(1, results.size)
        val result = results.first()
        assertEquals(PaymentFlowType.CHECKOUT_WITHOUT_CVC, result.flowType)
        assertTrue(result.status is AirwallexPaymentStatus.Failure)
        val failureStatus = result.status as AirwallexPaymentStatus.Failure
        assertNotNull(failureStatus.exception)
        assertEquals(
            "confirm with paymentConsent only support AirwallexPaymentSession",
            failureStatus.exception.message
        )

        job.cancel()
    }

    @Test
    fun `test confirmPaymentIntent with valid session`() = runTest {
        val session = createPaymentSession()
        val paymentConsent = mockk<PaymentConsent>(relaxed = true)
        val expectedStatus = AirwallexPaymentStatus.Success("test_payment_intent_id")

        val slot = slot<Airwallex.PaymentResultListener>()
        coEvery {
            airwallex.confirmPaymentIntent(
                session = session,
                paymentConsent = paymentConsent,
                listener = capture(slot)
            )
        } answers {
            slot.captured.onCompleted(expectedStatus)
        }

        val viewModel = createViewModel(session)
        val (results, job) = collectSharedFlow(viewModel.paymentResult)

        viewModel.confirmPaymentIntent(paymentConsent)
        advanceUntilIdle()

        assertEquals(1, results.size)
        val result = results.first()
        assertEquals(PaymentFlowType.CHECKOUT_WITHOUT_CVC, result.flowType)
        assertEquals(expectedStatus, result.status)

        job.cancel()
    }

    @Test
    fun `test confirmPaymentIntent with paymentMethod null`() = runTest {
        val session = createPaymentSession()
        val paymentConsent = createMockPaymentConsent(paymentMethod = null)

        val viewModel = createViewModel(session)
        val (results, job) = collectSharedFlow(viewModel.paymentResult)

        viewModel.confirmPaymentIntent(paymentConsent)
        advanceUntilIdle()

        assertEquals(1, results.size)
        val result = results.first()
        assertEquals(PaymentFlowType.CHECKOUT_WITHOUT_CVC, result.flowType)
        assertTrue(result.status is AirwallexPaymentStatus.Failure)
        val failureStatus = result.status as AirwallexPaymentStatus.Failure
        assertNotNull(failureStatus.exception)
        assertEquals(
            "Payment method is required",
            failureStatus.exception.message
        )

        job.cancel()
    }

    @Test
    fun `test confirmPaymentIntent with session is Session`() = runTest {
        val session = createSession()
        val paymentConsent = createMockPaymentConsent(id = "consent_id")
        val expectedStatus = AirwallexPaymentStatus.Success("test_payment_intent_id")

        val slot = slot<Airwallex.PaymentResultListener>()
        coEvery {
            airwallex.checkout(
                session = session,
                paymentMethod = any(),
                paymentConsent = paymentConsent,
                cvc = null,
                listener = capture(slot)
            )
        } answers {
            slot.captured.onCompleted(expectedStatus)
        }

        val viewModel = createViewModel(session)
        val (results, job) = collectSharedFlow(viewModel.paymentResult)

        viewModel.confirmPaymentIntent(paymentConsent)
        advanceUntilIdle()

        assertEquals(1, results.size)
        val result = results.first()
        assertEquals(PaymentFlowType.CHECKOUT_WITHOUT_CVC, result.flowType)
        assertEquals(expectedStatus, result.status)

        coVerify {
            airwallex.checkout(
                session = session,
                paymentMethod = any(),
                paymentConsent = paymentConsent,
                cvc = null,
                listener = any()
            )
        }

        job.cancel()
    }

    // ========== Checkout With CVC Tests ==========

    @Test
    fun `test checkoutWithCvc with valid payment consent and cvc`() = runTest {
        val session = createPaymentSession()
        val paymentConsent = createMockPaymentConsent(id = "consent_id")
        val cvc = "123"
        val expectedStatus = AirwallexPaymentStatus.Success("test_payment_intent_id")

        val slot = slot<Airwallex.PaymentResultListener>()
        coEvery {
            airwallex.checkout(
                session = session,
                paymentMethod = any(),
                paymentConsent = paymentConsent,
                cvc = cvc,
                flow = any(),
                listener = capture(slot)
            )
        } answers {
            slot.captured.onCompleted(expectedStatus)
        }

        val viewModel = createViewModel(session)
        val (results, job) = collectSharedFlow(viewModel.paymentResult)

        viewModel.checkoutWithCvc(paymentConsent, cvc)
        advanceUntilIdle()

        assertEquals(1, results.size)
        val result = results.first()
        assertEquals(PaymentFlowType.CHECKOUT_WITH_CVC, result.flowType)
        assertEquals(expectedStatus, result.status)

        job.cancel()
    }

    @Test
    fun `test checkoutWithCvc with null payment method in consent`() = runTest {
        val session = createPaymentSession()
        val paymentConsent = createMockPaymentConsent(paymentMethod = null)
        val cvc = "123"

        val viewModel = createViewModel(session)
        val (results, job) = collectSharedFlow(viewModel.paymentResult)

        viewModel.checkoutWithCvc(paymentConsent, cvc)
        advanceUntilIdle()

        assertEquals(1, results.size)
        val result = results.first()
        assertEquals(PaymentFlowType.CHECKOUT_WITH_CVC, result.flowType)
        assertTrue(result.status is AirwallexPaymentStatus.Failure)
        val failureStatus = result.status as AirwallexPaymentStatus.Failure
        assertNotNull(failureStatus.exception)
        assertEquals(
            "checkout with paymentConsent without paymentMethod",
            failureStatus.exception.message
        )

        job.cancel()
    }

    @Test
    fun `test checkoutWithCvc with non-payment session`() = runTest {
        val session = createRecurringSession()
        val paymentConsent = createMockPaymentConsent()
        val cvc = "123"
        val error = AirwallexCheckoutException(message = "checkout with paymentConsent only support AirwallexPaymentSession")
        val expectedStatus = AirwallexPaymentStatus.Failure(error)

        val slot = slot<Airwallex.PaymentResultListener>()
        coEvery {
            airwallex.checkout(
                session = session,
                paymentMethod = any(),
                paymentConsent = paymentConsent,
                cvc = cvc,
                flow = any(),
                listener = capture(slot)
            )
        } answers {
            slot.captured.onCompleted(expectedStatus)
        }

        val viewModel = createViewModel(session)
        val (results, job) = collectSharedFlow(viewModel.paymentResult)

        viewModel.checkoutWithCvc(paymentConsent, cvc)
        advanceUntilIdle()

        assertEquals(1, results.size)
        val result = results.first()
        assertEquals(PaymentFlowType.CHECKOUT_WITH_CVC, result.flowType)
        assertTrue(result.status is AirwallexPaymentStatus.Failure)
        val failureStatus = result.status as AirwallexPaymentStatus.Failure
        assertNotNull(failureStatus.exception)
        assertEquals(
            "checkout with paymentConsent only support AirwallexPaymentSession",
            failureStatus.exception.message
        )

        job.cancel()
    }

    // ========== Google Pay Tests ==========

    @Test
    fun `test checkoutWithGooglePay success`() = runTest {
        val session = createPaymentSession()
        val expectedStatus = AirwallexPaymentStatus.Success("test_payment_intent_id")

        val slot = slot<Airwallex.PaymentResultListener>()
        coEvery {
            airwallex.startGooglePay(
                session = session,
                listener = capture(slot)
            )
        } answers {
            slot.captured.onCompleted(expectedStatus)
        }

        val viewModel = createViewModel(session)
        val (results, job) = collectSharedFlow(viewModel.paymentResult)

        viewModel.checkoutWithGooglePay()
        advanceUntilIdle()

        assertEquals(1, results.size)
        val result = results.first()
        assertEquals(PaymentFlowType.CHECKOUT_WITH_GOOGLE_PAY, result.flowType)
        assertEquals(expectedStatus, result.status)

        job.cancel()
    }

    @Test
    fun `test checkoutWithGooglePay failure`() = runTest {
        val session = createPaymentSession()
        val error = AirwallexCheckoutException(message = "Test error")
        val expectedStatus = AirwallexPaymentStatus.Failure(error)

        val slot = slot<Airwallex.PaymentResultListener>()
        coEvery {
            airwallex.startGooglePay(
                session = session,
                listener = capture(slot)
            )
        } answers {
            slot.captured.onCompleted(expectedStatus)
        }

        val viewModel = createViewModel(session)
        val (results, job) = collectSharedFlow(viewModel.paymentResult)

        viewModel.checkoutWithGooglePay()
        advanceUntilIdle()

        assertEquals(1, results.size)
        val result = results.first()
        assertEquals(PaymentFlowType.CHECKOUT_WITH_GOOGLE_PAY, result.flowType)
        assertTrue(result.status is AirwallexPaymentStatus.Failure)
        val failureStatus = result.status as AirwallexPaymentStatus.Failure
        assertNotNull(failureStatus.exception)
        assertEquals("Test error", failureStatus.exception.message)

        job.cancel()
    }

    // ========== Checkout With New Card Tests ==========

    @Test
    fun `test checkoutWithNewCard success with saveCard true and billing`() = runTest {
        val session = createPaymentSession()
        val card = mockk<PaymentMethod.Card>(relaxed = true)
        val billing = mockk<Billing>(relaxed = true)
        val expectedStatus = AirwallexPaymentStatus.Success("test_payment_intent_id")

        val slot = slot<Airwallex.PaymentResultListener>()
        coEvery {
            airwallex.confirmPaymentIntent(
                session = session,
                card = card,
                billing = billing,
                saveCard = true,
                listener = capture(slot)
            )
        } answers {
            slot.captured.onCompleted(expectedStatus)
        }

        val viewModel = createViewModel(session)
        val (results, job) = collectSharedFlow(viewModel.paymentResult)

        viewModel.checkoutWithNewCard(card, saveCard = true, billing = billing)
        advanceUntilIdle()

        assertEquals(1, results.size)
        val result = results.first()
        assertEquals(PaymentFlowType.CHECKOUT_WITH_NEW_CARD, result.flowType)
        assertEquals(expectedStatus, result.status)

        coVerify {
            airwallex.confirmPaymentIntent(
                session = session,
                card = card,
                billing = billing,
                saveCard = true,
                listener = any()
            )
        }

        job.cancel()
    }

    @Test
    fun `test checkoutWithNewCard with saveCard false`() = runTest {
        val session = createPaymentSession()
        val card = mockk<PaymentMethod.Card>(relaxed = true)
        val expectedStatus = AirwallexPaymentStatus.Success("test_payment_intent_id")

        val slot = slot<Airwallex.PaymentResultListener>()
        coEvery {
            airwallex.confirmPaymentIntent(
                session = session,
                card = card,
                billing = null,
                saveCard = false,
                listener = capture(slot)
            )
        } answers {
            slot.captured.onCompleted(expectedStatus)
        }

        val viewModel = createViewModel(session)
        val (results, job) = collectSharedFlow(viewModel.paymentResult)

        viewModel.checkoutWithNewCard(card, saveCard = false, billing = null)
        advanceUntilIdle()

        assertEquals(1, results.size)
        assertEquals(expectedStatus, results.first().status)

        coVerify {
            airwallex.confirmPaymentIntent(
                session = session,
                card = card,
                billing = null,
                saveCard = false,
                listener = any()
            )
        }

        job.cancel()
    }

    @Test
    fun `test checkoutWithNewCard with non-payment session`() = runTest {
        val session = createRecurringSession()
        val card = mockk<PaymentMethod.Card>(relaxed = true)
        val expectedStatus = AirwallexPaymentStatus.Success("test_payment_intent_id", "test_consent_id")

        val slot = slot<Airwallex.PaymentResultListener>()
        coEvery {
            airwallex.confirmPaymentIntent(
                session = session,
                card = card,
                billing = null,
                saveCard = true,
                listener = capture(slot)
            )
        } answers {
            slot.captured.onCompleted(expectedStatus)
        }

        val viewModel = createViewModel(session)
        val (results, job) = collectSharedFlow(viewModel.paymentResult)

        viewModel.checkoutWithNewCard(card, saveCard = true, billing = null)
        advanceUntilIdle()

        assertEquals(1, results.size)
        val result = results.first()
        assertEquals(PaymentFlowType.CHECKOUT_WITH_NEW_CARD, result.flowType)
        assertEquals(expectedStatus, result.status)
        job.cancel()
    }

    @Test
    fun `test checkoutWithNewCard with session is Session`() = runTest {
        val session = createSession()
        val card = mockk<PaymentMethod.Card>(relaxed = true) {
            every { cvc } returns "123"
        }
        val billing = mockk<Billing>(relaxed = true)
        val expectedStatus = AirwallexPaymentStatus.Success("test_payment_intent_id")

        val slot = slot<Airwallex.PaymentResultListener>()
        coEvery {
            airwallex.checkout(
                session = session,
                paymentMethod = any(),
                cvc = "123",
                saveCard = true,
                listener = capture(slot)
            )
        } answers {
            slot.captured.onCompleted(expectedStatus)
        }

        val viewModel = createViewModel(session)
        val (results, job) = collectSharedFlow(viewModel.paymentResult)

        viewModel.checkoutWithNewCard(card, saveCard = true, billing = billing)
        advanceUntilIdle()

        assertEquals(1, results.size)
        val result = results.first()
        assertEquals(PaymentFlowType.CHECKOUT_WITH_NEW_CARD, result.flowType)
        assertEquals(expectedStatus, result.status)

        coVerify {
            airwallex.checkout(
                session = session,
                paymentMethod = match {
                    it.type == PaymentMethodType.CARD.value &&
                    it.card == card &&
                    it.billing == billing
                },
                cvc = "123",
                saveCard = true,
                listener = any()
            )
        }

        job.cancel()
    }

    @Test
    fun `test updateActivity updates airwallex activity`() = runTest {
        val session = createPaymentSession()
        val viewModel = createViewModel(session)
        val newActivity = mockk<androidx.activity.ComponentActivity>(relaxed = true)

        every { airwallex.updateActivity(newActivity) } just Runs

        viewModel.updateActivity(newActivity)

        verify(exactly = 1) {
            airwallex.updateActivity(newActivity)
        }
    }

    // ========== Analytics Tests ==========

    @Test
    fun `test trackScreenViewed calls AnalyticsLogger`() = runTest {
        mockkObject(AnalyticsLogger)
        every { AnalyticsLogger.logPaymentView(any(), any()) } just Runs

        val session = createPaymentSession()
        val viewModel = createViewModel(session)

        val testAdditionalInfo = mapOf(
            "key1" to "value1",
            "key2" to "value2"
        )

        viewModel.trackScreenViewed("payment_screen", testAdditionalInfo)

        verify(exactly = 1) {
            AnalyticsLogger.logPaymentView(
                viewName = "payment_screen",
                additionalInfo = testAdditionalInfo
            )
        }
    }

    @Test
    fun `test Factory creates ViewModel correctly`() {
        val session = createPaymentSession()
        val factory = PaymentFlowViewModel.Factory(airwallex, session)

        val viewModel = factory.create(PaymentFlowViewModel::class.java)

        assertNotNull(viewModel)
        assertTrue(viewModel is PaymentFlowViewModel)
        assertTrue(viewModel.availablePaymentMethods.value.isEmpty())
        assertTrue(viewModel.availablePaymentConsents.value.isEmpty())
    }

    // ========== Helper Method Tests ==========

    private fun createPaymentSession(): AirwallexPaymentSession {
        val paymentIntent = PaymentIntent(
            id = "test_payment_intent_id",
            amount = BigDecimal("100.00"),
            currency = "USD",
            clientSecret = "test_client_secret",
            status = PaymentIntentStatus.REQUIRES_PAYMENT_METHOD,
            customerId = "test_customer_id"
        )

        return AirwallexPaymentSession.Builder(
            paymentIntent = paymentIntent,
            countryCode = "US"
        ).build()
    }

    private fun createRecurringSession(): AirwallexRecurringSession {
        return AirwallexRecurringSession.Builder(
            customerId = "test_customer_id",
            currency = "USD",
            amount = BigDecimal("100.00"),
            countryCode = "US",
            nextTriggerBy = PaymentConsent.NextTriggeredBy.CUSTOMER,
            clientSecret = "test_client_secret"
        ).build()
    }

    private fun createSession(): Session {
        val paymentIntent = PaymentIntent(
            id = "test_payment_intent_id",
            amount = BigDecimal("100.00"),
            currency = "USD",
            clientSecret = "test_client_secret",
            status = PaymentIntentStatus.REQUIRES_PAYMENT_METHOD,
            customerId = "test_customer_id"
        )

        return Session.Builder(
            paymentIntent = paymentIntent,
            countryCode = "US"
        ).build()
    }
}
