package com.airwallex.android.view

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexRecurringSession
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.Billing
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PaymentIntentStatus
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.view.Constants.createPaymentMethod
import com.airwallex.android.view.PaymentOperationsViewModel.PaymentOperationType
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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

class PaymentOperationsViewModelTest {
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
            }
        )

        coEvery {
            airwallex.fetchAvailablePaymentMethodsAndConsents(session)
        } returns Result.success(Pair(mockMethods, mockConsents))

        val viewModel = PaymentOperationsViewModel(airwallex, session)
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

        val viewModel = PaymentOperationsViewModel(airwallex, session)
        val result = viewModel.fetchAvailablePaymentMethodsAndConsents()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AirwallexCheckoutException)
        // StateFlows should remain empty on failure
        assertTrue(viewModel.availablePaymentMethods.value.isEmpty())
        assertTrue(viewModel.availablePaymentConsents.value.isEmpty())
    }

    @Test
    fun `test deletePaymentConsent success`() = runTest {
        val session = createPaymentSession()
        val paymentConsent = mockk<PaymentConsent> {
            every { id } returns "consent_id"
        }

        val slot = slot<Airwallex.PaymentListener<PaymentConsent>>()
        coEvery {
            airwallex.getClientSecret(session)
        } returns "test_client_secret"

        coEvery {
            airwallex.disablePaymentConsent(any(), capture(slot))
        } answers {
            slot.captured.onSuccess(paymentConsent)
        }

        val viewModel = PaymentOperationsViewModel(airwallex, session)

        // Collect the SharedFlow in background
        val results = mutableListOf<PaymentOperationsViewModel.DeleteConsentResult>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.deleteConsentResult.collect { results.add(it) }
        }

        viewModel.deletePaymentConsent(paymentConsent)
        advanceUntilIdle()

        assertEquals(1, results.size)
        val result = results.first()
        assertTrue(result is PaymentOperationsViewModel.DeleteConsentResult.Success)
        assertEquals(paymentConsent, (result as PaymentOperationsViewModel.DeleteConsentResult.Success).consent)

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
        val paymentConsent = mockk<PaymentConsent> {
            every { id } returns "consent_id"
        }

        coEvery {
            airwallex.getClientSecret(session)
        } returns null

        val viewModel = PaymentOperationsViewModel(airwallex, session)

        val results = mutableListOf<PaymentOperationsViewModel.DeleteConsentResult>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.deleteConsentResult.collect { results.add(it) }
        }

        viewModel.deletePaymentConsent(paymentConsent)
        advanceUntilIdle()

        assertEquals(1, results.size)
        val result = results.first()
        assertTrue(result is PaymentOperationsViewModel.DeleteConsentResult.Failure)
        assertTrue((result as PaymentOperationsViewModel.DeleteConsentResult.Failure).exception is AirwallexCheckoutException)
        assertEquals("clientSecret is null", result.exception.message)

        job.cancel()
    }

    @Test
    fun `test deletePaymentConsent calls onFailed on exception`() = runTest {
        val session = createPaymentSession()
        val paymentConsent = mockk<PaymentConsent> {
            every { id } returns "test_consent_id"
        }

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

        val viewModel = PaymentOperationsViewModel(airwallex, session)

        val results = mutableListOf<PaymentOperationsViewModel.DeleteConsentResult>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.deleteConsentResult.collect { results.add(it) }
        }

        viewModel.deletePaymentConsent(paymentConsent)
        advanceUntilIdle()

        assertEquals(1, results.size)
        val result = results.first()
        assertTrue(result is PaymentOperationsViewModel.DeleteConsentResult.Failure)
        assertTrue((result as PaymentOperationsViewModel.DeleteConsentResult.Failure).exception is AirwallexCheckoutException)

        job.cancel()
    }

    @Test
    fun `test confirmPaymentIntent with invalid session`() = runTest {
        val session = createRecurringSession()
        val paymentConsent = mockk<PaymentConsent>(relaxed = true)

        val viewModel = PaymentOperationsViewModel(airwallex, session)

        val results = mutableListOf<PaymentOperationsViewModel.PaymentResultEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.paymentResult.collect { results.add(it) }
        }

        viewModel.confirmPaymentIntent(paymentConsent)
        advanceUntilIdle()

        assertEquals(1, results.size)
        val result = results.first()
        assertEquals(PaymentOperationType.CHECKOUT_WITHOUT_CVC, result.operationType)
        assertTrue(result.status is AirwallexPaymentStatus.Failure)
        assertEquals(
            "confirm with paymentConsent only support AirwallexPaymentSession",
            (result.status as AirwallexPaymentStatus.Failure).exception.message
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

        val viewModel = PaymentOperationsViewModel(airwallex, session)

        val results = mutableListOf<PaymentOperationsViewModel.PaymentResultEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.paymentResult.collect { results.add(it) }
        }

        viewModel.confirmPaymentIntent(paymentConsent)
        advanceUntilIdle()

        assertEquals(1, results.size)
        val result = results.first()
        assertEquals(PaymentOperationType.CHECKOUT_WITHOUT_CVC, result.operationType)
        assertEquals(expectedStatus, result.status)

        job.cancel()
    }

    @Test
    fun `test checkoutWithCvc with valid payment consent and cvc`() = runTest {
        val session = createPaymentSession()
        val paymentConsent = mockk<PaymentConsent> {
            every { paymentMethod } returns createPaymentMethod("card")
            every { id } returns "consent_id"
        }
        val cvc = "123"
        val expectedStatus = AirwallexPaymentStatus.Success("test_payment_intent_id")

        val slot = slot<Airwallex.PaymentResultListener>()
        coEvery {
            airwallex.checkout(
                session = session,
                paymentMethod = any(),
                paymentConsentId = "consent_id",
                cvc = cvc,
                flow = any(),
                listener = capture(slot)
            )
        } answers {
            slot.captured.onCompleted(expectedStatus)
        }

        val viewModel = PaymentOperationsViewModel(airwallex, session)

        val results = mutableListOf<PaymentOperationsViewModel.PaymentResultEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.paymentResult.collect { results.add(it) }
        }

        viewModel.checkoutWithCvc(paymentConsent, cvc)
        advanceUntilIdle()

        assertEquals(1, results.size)
        val result = results.first()
        assertEquals(PaymentOperationType.CHECKOUT_WITH_CVC, result.operationType)
        assertEquals(expectedStatus, result.status)

        job.cancel()
    }

    @Test
    fun `test checkoutWithCvc with null payment method in consent`() = runTest {
        val session = createPaymentSession()
        val paymentConsent = mockk<PaymentConsent> {
            every { paymentMethod } returns null
        }
        val cvc = "123"

        val viewModel = PaymentOperationsViewModel(airwallex, session)

        val results = mutableListOf<PaymentOperationsViewModel.PaymentResultEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.paymentResult.collect { results.add(it) }
        }

        viewModel.checkoutWithCvc(paymentConsent, cvc)
        advanceUntilIdle()

        assertEquals(1, results.size)
        val result = results.first()
        assertEquals(PaymentOperationType.CHECKOUT_WITH_CVC, result.operationType)
        assertTrue(result.status is AirwallexPaymentStatus.Failure)
        assertEquals(
            "checkout with paymentConsent without paymentMethod",
            (result.status as AirwallexPaymentStatus.Failure).exception.message
        )

        job.cancel()
    }

    @Test
    fun `test checkoutWithCvc with non-payment session`() = runTest {
        val session = createRecurringSession()
        val paymentConsent = mockk<PaymentConsent> {
            every { paymentMethod } returns createPaymentMethod("card")
        }
        val cvc = "123"

        val viewModel = PaymentOperationsViewModel(airwallex, session)

        val results = mutableListOf<PaymentOperationsViewModel.PaymentResultEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.paymentResult.collect { results.add(it) }
        }

        viewModel.checkoutWithCvc(paymentConsent, cvc)
        advanceUntilIdle()

        assertEquals(1, results.size)
        val result = results.first()
        assertEquals(PaymentOperationType.CHECKOUT_WITH_CVC, result.operationType)
        assertTrue(result.status is AirwallexPaymentStatus.Failure)
        assertEquals(
            "checkout with paymentConsent only support AirwallexPaymentSession",
            (result.status as AirwallexPaymentStatus.Failure).exception.message
        )

        job.cancel()
    }

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

        val viewModel = PaymentOperationsViewModel(airwallex, session)

        val results = mutableListOf<PaymentOperationsViewModel.PaymentResultEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.paymentResult.collect { results.add(it) }
        }

        viewModel.checkoutWithGooglePay()
        advanceUntilIdle()

        assertEquals(1, results.size)
        val result = results.first()
        assertEquals(PaymentOperationType.CHECKOUT_WITH_GOOGLE_PAY, result.operationType)
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

        val viewModel = PaymentOperationsViewModel(airwallex, session)

        val results = mutableListOf<PaymentOperationsViewModel.PaymentResultEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.paymentResult.collect { results.add(it) }
        }

        viewModel.checkoutWithGooglePay()
        advanceUntilIdle()

        assertEquals(1, results.size)
        val result = results.first()
        assertEquals(PaymentOperationType.CHECKOUT_WITH_GOOGLE_PAY, result.operationType)
        assertTrue(result.status is AirwallexPaymentStatus.Failure)

        job.cancel()
    }

    @Test
    fun `test checkoutWithNewCard success`() = runTest {
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

        val viewModel = PaymentOperationsViewModel(airwallex, session)

        val results = mutableListOf<PaymentOperationsViewModel.PaymentResultEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.paymentResult.collect { results.add(it) }
        }

        viewModel.checkoutWithNewCard(card, saveCard = true, billing = billing)
        advanceUntilIdle()

        assertEquals(1, results.size)
        val result = results.first()
        assertEquals(PaymentOperationType.CHECKOUT_WITH_NEW_CARD, result.operationType)
        assertEquals(expectedStatus, result.status)

        job.cancel()
    }

    @Test
    fun `test checkoutWithNewCard with non-payment session`() = runTest {
        val session = createRecurringSession()
        val card = mockk<PaymentMethod.Card>(relaxed = true)

        val viewModel = PaymentOperationsViewModel(airwallex, session)

        val results = mutableListOf<PaymentOperationsViewModel.PaymentResultEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.paymentResult.collect { results.add(it) }
        }

        viewModel.checkoutWithNewCard(card, saveCard = true, billing = null)
        advanceUntilIdle()

        assertEquals(1, results.size)
        val result = results.first()
        assertEquals(PaymentOperationType.CHECKOUT_WITH_NEW_CARD, result.operationType)
        assertTrue(result.status is AirwallexPaymentStatus.Failure)
        assertEquals(
            "checkout with new card only supports AirwallexPaymentSession",
            (result.status as AirwallexPaymentStatus.Failure).exception.message
        )

        job.cancel()
    }

    @Test
    fun `test trackScreenViewed`() = runTest {
        mockkObject(AnalyticsLogger)
        every { AnalyticsLogger.logPaymentView(any(), any()) } just Runs

        val session = createPaymentSession()
        val viewModel = PaymentOperationsViewModel(airwallex, session)

        viewModel.trackScreenViewed("test_screen", mapOf("key" to "value"))

        verify {
            AnalyticsLogger.logPaymentView(
                viewName = "test_screen",
                additionalInfo = mapOf("key" to "value")
            )
        }
    }

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
}
