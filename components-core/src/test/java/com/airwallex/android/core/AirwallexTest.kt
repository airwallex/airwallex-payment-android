package com.airwallex.android.core

import android.app.Application
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.Page
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentConsentFixtures
import com.airwallex.android.core.model.PaymentIntentFixtures
import com.airwallex.android.core.model.PaymentMethodFixtures
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.core.model.RetrieveAvailablePaymentConsentsParams
import com.airwallex.android.core.model.RetrieveAvailablePaymentMethodParams
import com.airwallex.android.core.model.TransactionMode
import com.airwallex.android.core.util.BuildHelper
import com.airwallex.risk.AirwallexRisk
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.slot
import io.mockk.spyk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Suppress("LargeClass")
class AirwallexTest {

    // Test dispatcher for coroutines
    private lateinit var testDispatcher: TestDispatcher

    // System under test
    private lateinit var airwallex: Airwallex

    // Mocked dependencies
    @MockK
    private lateinit var mockActivity: ComponentActivity

    @MockK
    private lateinit var mockFragment: Fragment

    @MockK
    private lateinit var mockPaymentManager: PaymentManager

    @MockK
    private lateinit var mockApplicationContext: Context

    @RelaxedMockK
    private lateinit var mockApplication: Application

    @MockK
    private lateinit var mockLifecycleOwner: LifecycleOwner

    @MockK
    private lateinit var mockLifecycle: Lifecycle

    private lateinit var lifecycleRegistry: LifecycleRegistry

    // Mock sessions
    @RelaxedMockK
    private lateinit var mockPaymentSession: AirwallexPaymentSession

    @RelaxedMockK
    private lateinit var mockRecurringSession: AirwallexRecurringSession

    @RelaxedMockK
    private lateinit var mockRecurringWithIntentSession: AirwallexRecurringWithIntentSession

    // Mock action component providers
    @RelaxedMockK
    private lateinit var mockCardProvider: ActionComponentProvider<out ActionComponent>

    @RelaxedMockK
    private lateinit var mockGooglePayProvider: ActionComponentProvider<out ActionComponent>

    @RelaxedMockK
    private lateinit var mockRedirectProvider: ActionComponentProvider<out ActionComponent>

    @RelaxedMockK
    private lateinit var mockActionComponent: ActionComponent

    // Test fixtures
    private val testPaymentIntent = PaymentIntentFixtures.PAYMENT_INTENT
    private val testPaymentMethod = PaymentMethodFixtures.PAYMENT_METHOD
    private val testPaymentConsent = PaymentConsentFixtures.PAYMENTCONSENT

    // Test data
    private val testClientSecret = "test_client_secret_abc123"
    private val testCustomerId = "cus_test_customer_id"
    private val testPaymentIntentId = "int_test_payment_intent_id"
    private val testPaymentConsentId = "cst_test_consent_id"
    private val testCurrency = "USD"
    private val testCvc = "123"

    @Before
    fun setUp() {
        // Initialize MockK annotations
        MockKAnnotations.init(this, relaxUnitFun = true)

        // Set up test coroutine dispatcher
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        // Mock static objects
        mockkObject(AirwallexPlugins)
        mockkObject(AnalyticsLogger)
        mockkObject(AirwallexLogger)
        mockkObject(AirwallexRisk)
        mockkObject(PaymentIntentProviderRepository)
        mockkObject(BuildHelper)

        // Mock AnalyticsLogger static methods
        every { AnalyticsLogger.initialize(any()) } just runs
        every { AnalyticsLogger.setSessionInformation(any(), any()) } just runs
        every { AnalyticsLogger.logAction(any(), any()) } just runs
        every { AnalyticsLogger.logPageView(any(), any()) } just runs
        every { AnalyticsLogger.logPaymentView(any(), any()) } just runs
        every { AnalyticsLogger.logError(any(), any<Map<String, Any>>()) } just runs
//        every { AnalyticsLogger.logError(any(), any<Exception>()) } just runs

        // Mock AirwallexLogger static methods
        every { AirwallexLogger.debug(any()) } just runs
        every { AirwallexLogger.info(any()) } just runs
        every { AirwallexLogger.error(any<String>()) } just runs
        every { AirwallexLogger.error(any(), any()) } just runs
        every { AirwallexLogger.initialize(any(), any(), any()) } just runs

        // Mock AirwallexRisk static methods
        every { AirwallexRisk.sessionId } returns mockk(relaxed = true)
        every { AirwallexRisk.start(any(), any(), any()) } just runs
//        every { AirwallexRisk.log(any(), any()) } just runs

        // Mock PaymentIntentProviderRepository
        every { PaymentIntentProviderRepository.initialize(any()) } just runs

        // Set up application context - Required for Airwallex initialization
        every { mockApplicationContext.applicationContext } returns mockApplicationContext
        every { mockActivity.applicationContext } returns mockApplicationContext

        // Set up action component providers
        every { mockCardProvider.get() } returns mockActionComponent
        every { mockGooglePayProvider.get() } returns mockActionComponent
        every { mockRedirectProvider.get() } returns mockActionComponent
        coEvery { mockCardProvider.canHandleSessionAndPaymentMethod(any(), any(), any()) } returns true
        coEvery { mockGooglePayProvider.canHandleSessionAndPaymentMethod(any(), any(), any()) } returns true

        // Mock AirwallexPlugins methods needed for retrieveAvailablePaymentMethods
        every { AirwallexPlugins.getProvider(any<AvailablePaymentMethodType>()) } returns mockCardProvider

        // Set up session mocks - Only properties needed for our tests
        every { mockPaymentSession.paymentIntent } returns testPaymentIntent
        every { mockPaymentSession.hidePaymentConsents } returns false

        every { mockRecurringSession.clientSecret } returns testClientSecret

        every { mockRecurringWithIntentSession.paymentIntent } returns testPaymentIntent

        // Create Airwallex instance with mocked dependencies
        airwallex = Airwallex(
            fragment = null,
            activity = mockActivity,
            paymentManager = mockPaymentManager,
            applicationContext = mockApplicationContext
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // Tests for getPaymentIntent()
    @Test
    fun `getPaymentIntent returns paymentIntent for AirwallexPaymentSession`() {
        val result = airwallex.getPaymentIntent(mockPaymentSession)

        assertEquals(testPaymentIntent, result)
    }

    @Test
    fun `getPaymentIntent returns paymentIntent for AirwallexRecurringWithIntentSession`() {
        val result = airwallex.getPaymentIntent(mockRecurringWithIntentSession)

        assertEquals(testPaymentIntent, result)
    }

    @Test
    fun `getPaymentIntent returns null for AirwallexRecurringSession`() {
        val result = airwallex.getPaymentIntent(mockRecurringSession)

        assertEquals(null, result)
    }

    @Test(expected = Exception::class)
    fun `getPaymentIntent throws exception for unsupported session type`() {
        val unsupportedSession = mockk<AirwallexSession>()
        airwallex.getPaymentIntent(unsupportedSession)
    }

    // Tests for getClientSecret()
    @Test
    fun `getClientSecret returns clientSecret for AirwallexPaymentSession`() {
        val result = airwallex.getClientSecret(mockPaymentSession)

        assertEquals(testPaymentIntent.clientSecret, result)
    }

    @Test
    fun `getClientSecret returns clientSecret for AirwallexRecurringWithIntentSession`() {
        val result = airwallex.getClientSecret(mockRecurringWithIntentSession)

        assertEquals(testPaymentIntent.clientSecret, result)
    }

    @Test
    fun `getClientSecret returns clientSecret for AirwallexRecurringSession`() {
        val result = airwallex.getClientSecret(mockRecurringSession)

        assertEquals(testClientSecret, result)
    }

    @Test
    fun `getClientSecret returns null for unsupported session type`() {
        val unsupportedSession = mockk<AirwallexSession>()
        val result = airwallex.getClientSecret(unsupportedSession)

        assertEquals(null, result)
    }

    // Tests for shouldHidePaymentConsents()
    @Test
    fun `shouldHidePaymentConsents returns true when hidePaymentConsents is true`() {
        every { mockPaymentSession.hidePaymentConsents } returns true

        val result = airwallex.shouldHidePaymentConsents(mockPaymentSession)

        assertEquals(true, result)
    }

    @Test
    fun `shouldHidePaymentConsents returns false when hidePaymentConsents is false`() {
        every { mockPaymentSession.hidePaymentConsents } returns false

        val result = airwallex.shouldHidePaymentConsents(mockPaymentSession)

        assertEquals(false, result)
    }

    @Test
    fun `shouldHidePaymentConsents returns false for AirwallexRecurringSession`() {
        val result = airwallex.shouldHidePaymentConsents(mockRecurringSession)

        assertEquals(false, result)
    }

    @Test
    fun `shouldHidePaymentConsents returns false for AirwallexRecurringWithIntentSession`() {
        val result = airwallex.shouldHidePaymentConsents(mockRecurringWithIntentSession)

        assertEquals(false, result)
    }

    @Test
    fun `shouldHidePaymentConsents returns false for unsupported session type`() {
        val unsupportedSession = mockk<AirwallexSession>()
        val result = airwallex.shouldHidePaymentConsents(unsupportedSession)

        assertEquals(false, result)
    }

    // Tests for getSupportedCardSchemes()
    @Test
    fun `getSupportedCardSchemes returns card schemes when card payment method exists`() {
        val cardScheme1 = mockk<com.airwallex.android.core.model.CardScheme> {
            every { name } returns "visa"
        }
        val cardScheme2 = mockk<com.airwallex.android.core.model.CardScheme> {
            every { name } returns "mastercard"
        }
        val cardSchemes = listOf(cardScheme1, cardScheme2)

        val cardPaymentMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
            every { this@mockk.cardSchemes } returns cardSchemes
        }
        val otherPaymentMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns "wechatpay"
            every { this@mockk.cardSchemes } returns null
        }

        val paymentMethods = listOf(otherPaymentMethod, cardPaymentMethod)
        val result = airwallex.getSupportedCardSchemes(paymentMethods)

        assertEquals(cardSchemes, result)
    }

    @Test
    fun `getSupportedCardSchemes returns empty list when no card payment method exists`() {
        val paymentMethod1 = mockk<AvailablePaymentMethodType> {
            every { name } returns "wechatpay"
            every { cardSchemes } returns null
        }
        val paymentMethod2 = mockk<AvailablePaymentMethodType> {
            every { name } returns "alipay"
            every { cardSchemes } returns null
        }

        val paymentMethods = listOf(paymentMethod1, paymentMethod2)
        val result = airwallex.getSupportedCardSchemes(paymentMethods)

        assertEquals(emptyList(), result)
    }

    @Test
    fun `getSupportedCardSchemes returns empty list when input list is empty`() {
        val result = airwallex.getSupportedCardSchemes(emptyList())

        assertEquals(emptyList(), result)
    }

    @Test
    fun `getSupportedCardSchemes returns empty list when card payment method has null cardSchemes`() {
        val cardPaymentMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
            every { cardSchemes } returns null
        }

        val paymentMethods = listOf(cardPaymentMethod)
        val result = airwallex.getSupportedCardSchemes(paymentMethods)

        assertEquals(emptyList(), result)
    }

    // Tests for retrieveAvailablePaymentMethods()
    @Test
    fun `retrieveAvailablePaymentMethods returns filtered payment methods for AirwallexPaymentSession`() = runTest {
        // Create mock payment methods
        val cardPaymentMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
            every { transactionMode } returns TransactionMode.ONE_OFF
        }
        val wechatPaymentMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns "wechatpay"
            every { transactionMode } returns TransactionMode.ONE_OFF
        }
        val recurringOnlyMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns "somemethod"
            every { transactionMode } returns TransactionMode.RECURRING
        }

        // Create response with all methods - use mutable list to support setter
        val itemsList = mutableListOf(cardPaymentMethod, wechatPaymentMethod, recurringOnlyMethod)
        val mockResponse = mockk<Page<AvailablePaymentMethodType>> {
            every { items } returns itemsList
            every { items = any() } answers { itemsList.clear(); itemsList.addAll(firstArg()) }
            every { hasMore } returns false
        }

        // Mock payment manager to return the response
        coEvery {
            mockPaymentManager.retrieveAvailablePaymentMethods(any())
        } returns mockResponse

        // Create params
        val params = RetrieveAvailablePaymentMethodParams.Builder(
            clientSecret = testClientSecret,
            pageNum = 0
        ).build()

        // Execute
        val result = airwallex.retrieveAvailablePaymentMethods(mockPaymentSession, params)

        // Verify - should filter out RECURRING mode method
        assertEquals(2, result.items.size)
        assertEquals(false, result.items.contains(recurringOnlyMethod))
    }

    @Test
    fun `retrieveAvailablePaymentMethods returns filtered payment methods for AirwallexRecurringSession`() = runTest {
        // Create mock payment methods
        val recurringMethod1 = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
            every { transactionMode } returns TransactionMode.RECURRING
        }
        val recurringMethod2 = mockk<AvailablePaymentMethodType> {
            every { name } returns "wechatpay"
            every { transactionMode } returns TransactionMode.RECURRING
        }
        val oneOffOnlyMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns "somemethod"
            every { transactionMode } returns TransactionMode.ONE_OFF
        }

        // Create response with all methods - use mutable list to support setter
        val itemsList = mutableListOf(recurringMethod1, recurringMethod2, oneOffOnlyMethod)
        val mockResponse = mockk<Page<AvailablePaymentMethodType>> {
            every { items } returns itemsList
            every { items = any() } answers { itemsList.clear(); itemsList.addAll(firstArg()) }
            every { hasMore } returns false
        }

        // Mock payment manager to return the response
        coEvery {
            mockPaymentManager.retrieveAvailablePaymentMethods(any())
        } returns mockResponse

        // Create params
        val params = RetrieveAvailablePaymentMethodParams.Builder(
            clientSecret = testClientSecret,
            pageNum = 0
        ).build()

        // Execute
        val result = airwallex.retrieveAvailablePaymentMethods(mockRecurringSession, params)

        // Verify - should filter out ONE_OFF mode method
        assertEquals(2, result.items.size)
        assertEquals(false, result.items.contains(oneOffOnlyMethod))
    }

    @Test
    fun `retrieveAvailablePaymentMethods returns filtered payment methods for AirwallexRecurringWithIntentSession`() = runTest {
        // Create mock payment methods
        val recurringMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
            every { transactionMode } returns TransactionMode.RECURRING
        }
        val oneOffMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns "somemethod"
            every { transactionMode } returns TransactionMode.ONE_OFF
        }

        // Create response with both methods - use mutable list to support setter
        val itemsList = mutableListOf(recurringMethod, oneOffMethod)
        val mockResponse = mockk<Page<AvailablePaymentMethodType>> {
            every { items } returns itemsList
            every { items = any() } answers { itemsList.clear(); itemsList.addAll(firstArg()) }
            every { hasMore } returns false
        }

        // Mock payment manager to return the response
        coEvery {
            mockPaymentManager.retrieveAvailablePaymentMethods(any())
        } returns mockResponse

        // Create params
        val params = RetrieveAvailablePaymentMethodParams.Builder(
            clientSecret = testClientSecret,
            pageNum = 0
        ).build()

        // Execute
        val result = airwallex.retrieveAvailablePaymentMethods(mockRecurringWithIntentSession, params)

        // Verify - should only include RECURRING mode methods
        assertEquals(1, result.items.size)
        assertEquals(recurringMethod, result.items[0])
    }

    @Test
    fun `retrieveAvailablePaymentMethods filters out payment methods not supported by provider`() = runTest {
        // Create mock payment methods
        val supportedMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
            every { transactionMode } returns TransactionMode.ONE_OFF
        }
        val unsupportedMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns "unsupported"
            every { transactionMode } returns TransactionMode.ONE_OFF
        }

        // Create response - use mutable list to support setter
        val itemsList = mutableListOf(supportedMethod, unsupportedMethod)
        val mockResponse = mockk<Page<AvailablePaymentMethodType>> {
            every { items } returns itemsList
            every { items = any() } answers { itemsList.clear(); itemsList.addAll(firstArg()) }
            every { hasMore } returns false
        }

        // Mock payment manager
        coEvery {
            mockPaymentManager.retrieveAvailablePaymentMethods(any())
        } returns mockResponse

        // Mock provider to return null for unsupported method
        every { AirwallexPlugins.getProvider(supportedMethod) } returns mockCardProvider
        every { AirwallexPlugins.getProvider(unsupportedMethod) } returns null

        // Create params
        val params = RetrieveAvailablePaymentMethodParams.Builder(
            clientSecret = testClientSecret,
            pageNum = 0
        ).build()

        // Execute
        val result = airwallex.retrieveAvailablePaymentMethods(mockPaymentSession, params)

        // Verify - should filter out unsupported method
        assertEquals(1, result.items.size)
        assertEquals(supportedMethod, result.items[0])
    }

    @Test
    fun `retrieveAvailablePaymentMethods filters out payment methods that cannot handle session`() = runTest {
        // Create mock payment methods
        val canHandleMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
            every { transactionMode } returns TransactionMode.ONE_OFF
        }
        val cannotHandleMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns "restrictedmethod"
            every { transactionMode } returns TransactionMode.ONE_OFF
        }

        // Create response - use mutable list to support setter
        val itemsList = mutableListOf(canHandleMethod, cannotHandleMethod)
        val mockResponse = mockk<Page<AvailablePaymentMethodType>> {
            every { items } returns itemsList
            every { items = any() } answers { itemsList.clear(); itemsList.addAll(firstArg()) }
            every { hasMore } returns false
        }

        // Mock payment manager
        coEvery {
            mockPaymentManager.retrieveAvailablePaymentMethods(any())
        } returns mockResponse

        // Create mock providers with different capabilities
        val canHandleProvider = mockk<ActionComponentProvider<out ActionComponent>> {
            every { get() } returns mockActionComponent
            coEvery { canHandleSessionAndPaymentMethod(any(), any(), any()) } returns true
        }
        val cannotHandleProvider = mockk<ActionComponentProvider<out ActionComponent>> {
            every { get() } returns mockActionComponent
            coEvery { canHandleSessionAndPaymentMethod(any(), any(), any()) } returns false
        }

        // Mock plugin providers
        every { AirwallexPlugins.getProvider(canHandleMethod) } returns canHandleProvider
        every { AirwallexPlugins.getProvider(cannotHandleMethod) } returns cannotHandleProvider

        // Create params
        val params = RetrieveAvailablePaymentMethodParams.Builder(
            clientSecret = testClientSecret,
            pageNum = 0
        ).build()

        // Execute
        val result = airwallex.retrieveAvailablePaymentMethods(mockPaymentSession, params)

        // Verify - should filter out method that cannot handle session
        assertEquals(1, result.items.size)
        assertEquals(canHandleMethod, result.items[0])
    }

    @Test
    fun `retrieveAvailablePaymentMethods returns empty list when no methods match criteria`() = runTest {
        // Create mock payment methods that don't match
        val wrongModeMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns "somemethod"
            every { transactionMode } returns TransactionMode.RECURRING
        }

        // Create response - use mutable list to support setter
        val itemsList = mutableListOf(wrongModeMethod)
        val mockResponse = mockk<Page<AvailablePaymentMethodType>> {
            every { items } returns itemsList
            every { items = any() } answers { itemsList.clear(); itemsList.addAll(firstArg()) }
            every { hasMore } returns false
        }

        // Mock payment manager
        coEvery {
            mockPaymentManager.retrieveAvailablePaymentMethods(any())
        } returns mockResponse

        // Create params
        val params = RetrieveAvailablePaymentMethodParams.Builder(
            clientSecret = testClientSecret,
            pageNum = 0
        ).build()

        // Execute with payment session (ONE_OFF mode)
        val result = airwallex.retrieveAvailablePaymentMethods(mockPaymentSession, params)

        // Verify - should be empty
        assertEquals(0, result.items.size)
    }

    @Test(expected = com.airwallex.android.core.exception.AirwallexCheckoutException::class)
    fun `retrieveAvailablePaymentMethods throws exception for unsupported session type`() = runTest {
        val unsupportedSession = mockk<AirwallexSession>()

        val params = RetrieveAvailablePaymentMethodParams.Builder(
            clientSecret = testClientSecret,
            pageNum = 0
        ).build()

        airwallex.retrieveAvailablePaymentMethods(unsupportedSession, params)
    }

    // Tests for fetchAvailablePaymentMethodsAndConsents()
    @Test
    fun `fetchAvailablePaymentMethodsAndConsents returns methods and consents for payment session with customerId`() = runTest {
        // Create spy to mock private methods
        val airwallexSpy = spyk(airwallex)

        // Setup session
        every { mockPaymentSession.customerId } returns testCustomerId

        // Mock payment methods
        val cardMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
        }
        val wechatMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns "wechatpay"
        }
        val paymentMethods = listOf(cardMethod, wechatMethod)

        // Mock payment consents
        val consent1 = mockk<PaymentConsent> {
            every { paymentMethod } returns mockk {
                every { type } returns PaymentMethodType.CARD.value
            }
        }
        val consent2 = mockk<PaymentConsent> {
            every { paymentMethod } returns mockk {
                every { type } returns PaymentMethodType.CARD.value
            }
        }
        val paymentConsents = listOf(consent1, consent2)

        // Mock the private methods - we already tested these, so just return desired outcomes
        coEvery {
            airwallexSpy["retrieveAvailablePaymentMethodsPaged"](mockPaymentSession, testPaymentIntent.clientSecret)
        } returns paymentMethods

        coEvery {
            airwallexSpy["retrieveAvailablePaymentConsentsPaged"](testPaymentIntent.clientSecret, testCustomerId)
        } returns paymentConsents

        // Execute
        val result = airwallexSpy.fetchAvailablePaymentMethodsAndConsents(mockPaymentSession)

        // Verify
        assertNotNull(result)
        assertEquals(true, result.isSuccess)
        val (methods, consents) = result.getOrThrow()
        assertEquals(2, methods.size)
        assertEquals(2, consents.size)
    }

    @Test
    fun `fetchAvailablePaymentMethodsAndConsents returns methods without consents when customerId is null`() = runTest {
        // Create spy to mock private methods
        val airwallexSpy = spyk(airwallex)

        // Setup session without customerId
        every { mockPaymentSession.customerId } returns null

        // Mock payment methods
        val cardMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
        }
        val paymentMethods = listOf(cardMethod)

        // Mock the private methods
        coEvery {
            airwallexSpy["retrieveAvailablePaymentMethodsPaged"](mockPaymentSession, testPaymentIntent.clientSecret)
        } returns paymentMethods

        // Execute
        val result = airwallexSpy.fetchAvailablePaymentMethodsAndConsents(mockPaymentSession)

        // Verify
        assertNotNull(result)
        assertEquals(true, result.isSuccess)
        val (methods, consents) = result.getOrThrow()
        assertEquals(1, methods.size)
        assertEquals(0, consents.size) // No consents when customerId is null
    }

    @Test
    fun `fetchAvailablePaymentMethodsAndConsents returns methods without consents when hidePaymentConsents is true`() = runTest {
        // Create spy to mock private methods
        val airwallexSpy = spyk(airwallex)

        // Setup session with hidePaymentConsents = true
        every { mockPaymentSession.customerId } returns testCustomerId
        every { mockPaymentSession.hidePaymentConsents } returns true

        // Mock payment methods
        val cardMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
        }
        val paymentMethods = listOf(cardMethod)

        // Mock the private methods
        coEvery {
            airwallexSpy["retrieveAvailablePaymentMethodsPaged"](mockPaymentSession, testPaymentIntent.clientSecret)
        } returns paymentMethods

        // Execute
        val result = airwallexSpy.fetchAvailablePaymentMethodsAndConsents(mockPaymentSession)

        // Verify
        assertNotNull(result)
        assertEquals(true, result.isSuccess)
        val (methods, consents) = result.getOrThrow()
        assertEquals(1, methods.size)
        assertEquals(0, consents.size) // No consents when hidePaymentConsents is true
    }

    @Test
    fun `fetchAvailablePaymentMethodsAndConsents returns methods without consents for recurring session`() = runTest {
        // Create spy to mock private methods
        val airwallexSpy = spyk(airwallex)

        // Mock payment methods
        val cardMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
        }
        val paymentMethods = listOf(cardMethod)

        // Mock the private methods
        coEvery {
            airwallexSpy["retrieveAvailablePaymentMethodsPaged"](mockRecurringSession, testClientSecret)
        } returns paymentMethods

        // Execute
        val result = airwallexSpy.fetchAvailablePaymentMethodsAndConsents(mockRecurringSession)

        // Verify
        assertNotNull(result)
        assertEquals(true, result.isSuccess)
        val (methods, consents) = result.getOrThrow()
        assertEquals(1, methods.size)
        assertEquals(0, consents.size) // No consents for recurring session
    }

    @Test
    fun `fetchAvailablePaymentMethodsAndConsents filters methods by session paymentMethods list`() = runTest {
        // Create spy to mock private methods
        val airwallexSpy = spyk(airwallex)

        // Setup session with specific payment methods filter
        every { mockPaymentSession.customerId } returns null
        every { mockPaymentSession.paymentMethods } returns listOf("card", "wechatpay")

        // Mock payment methods - include one that should be filtered out
        val cardMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
        }
        val wechatMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns "wechatpay"
        }
        val alipayMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns "alipay"
        }
        val allMethods = listOf(cardMethod, wechatMethod, alipayMethod)

        // Mock the private methods
        coEvery {
            airwallexSpy["retrieveAvailablePaymentMethodsPaged"](mockPaymentSession, testPaymentIntent.clientSecret)
        } returns allMethods

        // Execute
        val result = airwallexSpy.fetchAvailablePaymentMethodsAndConsents(mockPaymentSession)

        // Verify - should only include card and wechatpay, not alipay
        assertNotNull(result)
        assertEquals(true, result.isSuccess)
        val (methods, _) = result.getOrThrow()
        assertEquals(2, methods.size)
        assertEquals(true, methods.any { it.name == PaymentMethodType.CARD.value })
        assertEquals(true, methods.any { it.name == "wechatpay" })
        assertEquals(false, methods.any { it.name == "alipay" })
    }

    @Test
    fun `fetchAvailablePaymentMethodsAndConsents filters consents to only card type for payment session`() = runTest {
        // Create spy to mock private methods
        val airwallexSpy = spyk(airwallex)

        // Setup session
        every { mockPaymentSession.customerId } returns testCustomerId

        // Mock payment methods - include card
        val cardMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
        }
        val paymentMethods = listOf(cardMethod)

        // Mock payment consents - include both card and non-card
        val cardConsent = mockk<PaymentConsent> {
            every { paymentMethod } returns mockk {
                every { type } returns PaymentMethodType.CARD.value
            }
        }
        val wechatConsent = mockk<PaymentConsent> {
            every { paymentMethod } returns mockk {
                every { type } returns "wechatpay"
            }
        }
        val allConsents = listOf(cardConsent, wechatConsent)

        // Mock the private methods
        coEvery {
            airwallexSpy["retrieveAvailablePaymentMethodsPaged"](mockPaymentSession, testPaymentIntent.clientSecret)
        } returns paymentMethods

        coEvery {
            airwallexSpy["retrieveAvailablePaymentConsentsPaged"](testPaymentIntent.clientSecret, testCustomerId)
        } returns allConsents

        // Execute
        val result = airwallexSpy.fetchAvailablePaymentMethodsAndConsents(mockPaymentSession)

        // Verify - should only include card consent
        assertNotNull(result)
        assertEquals(true, result.isSuccess)
        val (_, consents) = result.getOrThrow()
        assertEquals(1, consents.size)
        assertEquals(PaymentMethodType.CARD.value, consents[0].paymentMethod?.type)
    }

    @Test
    fun `fetchAvailablePaymentMethodsAndConsents returns failure when clientSecret is null`() = runTest {
        // Setup session with null client secret
        val sessionWithNullSecret = mockk<AirwallexPaymentSession> {
            every { paymentIntent } returns mockk {
                every { clientSecret } returns null
            }
        }

        // Execute
        val result = airwallex.fetchAvailablePaymentMethodsAndConsents(sessionWithNullSecret)

        // Verify
        assertNotNull(result)
        assertEquals(true, result.isFailure)
        assertEquals(true, result.exceptionOrNull() is AirwallexCheckoutException)
        assertEquals("Client secret is empty or blank", result.exceptionOrNull()?.message)
    }

    @Test
    fun `fetchAvailablePaymentMethodsAndConsents returns failure when clientSecret is blank`() = runTest {
        // Setup session with blank client secret
        val sessionWithBlankSecret = mockk<AirwallexPaymentSession> {
            every { paymentIntent } returns mockk {
                every { clientSecret } returns "   "
            }
        }

        // Execute
        val result = airwallex.fetchAvailablePaymentMethodsAndConsents(sessionWithBlankSecret)

        // Verify
        assertNotNull(result)
        assertEquals(true, result.isFailure)
        assertEquals(true, result.exceptionOrNull() is AirwallexCheckoutException)
        assertEquals("Client secret is empty or blank", result.exceptionOrNull()?.message)
    }

    @Test
    fun `fetchAvailablePaymentMethodsAndConsents returns failure when exception occurs`() = runTest {
        // Setup session
        every { mockPaymentSession.customerId } returns null

        // Mock paymentManager to throw exception (simulates retrieveAvailablePaymentMethodsPaged failure)
        val testException = AirwallexCheckoutException(message = "Test error")
        coEvery {
            mockPaymentManager.retrieveAvailablePaymentMethods(any())
        } throws testException

        // Execute
        val result = airwallex.fetchAvailablePaymentMethodsAndConsents(mockPaymentSession)

        // Verify
        assertNotNull(result)
        assertEquals(true, result.isFailure)
        assertEquals(true, result.exceptionOrNull() is AirwallexException)
        assertEquals("Test error", result.exceptionOrNull()?.message)
    }

    // Tests for retrieveAvailablePaymentConsentsPaged and loadPagedItems pagination logic
    @Test
    fun `pagination logic handles single page response`() = runTest {
        // Create spy to test pagination
        val airwallexSpy = spyk(airwallex)

        // Setup session with customerId to trigger consent retrieval
        every { mockPaymentSession.customerId } returns testCustomerId

        // Create single page response
        val consent1 = mockk<PaymentConsent> {
            every { paymentMethod } returns mockk {
                every { type } returns PaymentMethodType.CARD.value
            }
        }
        val consent2 = mockk<PaymentConsent> {
            every { paymentMethod } returns mockk {
                every { type } returns PaymentMethodType.CARD.value
            }
        }

        val singlePageResponse = mockk<Page<PaymentConsent>> {
            every { items } returns listOf(consent1, consent2)
            every { hasMore } returns false // Single page
        }

        // Mock retrieveAvailablePaymentConsents to return single page
        coEvery {
            airwallexSpy.retrieveAvailablePaymentConsents(any())
        } returns singlePageResponse

        // Mock payment methods
        val cardMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
        }
        coEvery {
            airwallexSpy["retrieveAvailablePaymentMethodsPaged"](mockPaymentSession, testPaymentIntent.clientSecret)
        } returns listOf(cardMethod)

        // Execute
        val result = airwallexSpy.fetchAvailablePaymentMethodsAndConsents(mockPaymentSession)

        // Verify - should have retrieved all items from single page
        assertEquals(true, result.isSuccess)
        val (_, consents) = result.getOrThrow()
        assertEquals(2, consents.size)

        // Verify retrieveAvailablePaymentConsents was called only once
        coVerify(exactly = 1) {
            airwallexSpy.retrieveAvailablePaymentConsents(any())
        }
    }

    @Test
    fun `pagination logic handles multiple pages`() = runTest {
        // Create spy to test pagination
        val airwallexSpy = spyk(airwallex)

        // Setup session with customerId
        every { mockPaymentSession.customerId } returns testCustomerId

        // Create consents for different pages
        val consent1 = mockk<PaymentConsent> {
            every { paymentMethod } returns mockk {
                every { type } returns PaymentMethodType.CARD.value
            }
        }
        val consent2 = mockk<PaymentConsent> {
            every { paymentMethod } returns mockk {
                every { type } returns PaymentMethodType.CARD.value
            }
        }
        val consent3 = mockk<PaymentConsent> {
            every { paymentMethod } returns mockk {
                every { type } returns PaymentMethodType.CARD.value
            }
        }

        // Mock first page (hasMore = true)
        val firstPageResponse = mockk<Page<PaymentConsent>> {
            every { items } returns listOf(consent1, consent2)
            every { hasMore } returns true
        }

        // Mock second page (hasMore = false)
        val secondPageResponse = mockk<Page<PaymentConsent>> {
            every { items } returns listOf(consent3)
            every { hasMore } returns false
        }

        // Mock retrieveAvailablePaymentConsents to return different pages based on pageNum
        coEvery {
            airwallexSpy.retrieveAvailablePaymentConsents(
                match { params ->
                    params.pageNum == 0
                }
            )
        } returns firstPageResponse

        coEvery {
            airwallexSpy.retrieveAvailablePaymentConsents(
                match { params ->
                    params.pageNum == 1
                }
            )
        } returns secondPageResponse

        // Mock payment methods
        val cardMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
        }
        coEvery {
            airwallexSpy["retrieveAvailablePaymentMethodsPaged"](mockPaymentSession, testPaymentIntent.clientSecret)
        } returns listOf(cardMethod)

        // Execute
        val result = airwallexSpy.fetchAvailablePaymentMethodsAndConsents(mockPaymentSession)

        // Verify - should have retrieved all items from both pages
        assertEquals(true, result.isSuccess)
        val (_, consents) = result.getOrThrow()
        assertEquals(3, consents.size) // 2 from first page + 1 from second page

        // Verify retrieveAvailablePaymentConsents was called twice (for 2 pages)
        coVerify(exactly = 2) {
            airwallexSpy.retrieveAvailablePaymentConsents(any())
        }
    }

    @Test
    fun `pagination logic handles empty first page`() = runTest {
        // Create spy to test pagination
        val airwallexSpy = spyk(airwallex)

        // Setup session with customerId
        every { mockPaymentSession.customerId } returns testCustomerId

        // Create empty page response
        val emptyPageResponse = mockk<Page<PaymentConsent>> {
            every { items } returns emptyList()
            every { hasMore } returns false
        }

        // Mock retrieveAvailablePaymentConsents to return empty page
        coEvery {
            airwallexSpy.retrieveAvailablePaymentConsents(any())
        } returns emptyPageResponse

        // Mock payment methods
        val cardMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
        }
        coEvery {
            airwallexSpy["retrieveAvailablePaymentMethodsPaged"](mockPaymentSession, testPaymentIntent.clientSecret)
        } returns listOf(cardMethod)

        // Execute
        val result = airwallexSpy.fetchAvailablePaymentMethodsAndConsents(mockPaymentSession)

        // Verify - should have empty consents list
        assertEquals(true, result.isSuccess)
        val (_, consents) = result.getOrThrow()
        assertEquals(0, consents.size)

        // Verify retrieveAvailablePaymentConsents was called once
        coVerify(exactly = 1) {
            airwallexSpy.retrieveAvailablePaymentConsents(any())
        }
    }

    @Test
    fun `pagination logic handles three pages correctly`() = runTest {
        val airwallexSpy = spyk(airwallex)
        every { mockPaymentSession.customerId } returns testCustomerId

        // Create test data
        val consents = createCardConsents(count = 4)
        val pages = listOf(
            createConsentPage(listOf(consents[0]), hasMore = true),
            createConsentPage(listOf(consents[1], consents[2]), hasMore = true),
            createConsentPage(listOf(consents[3]), hasMore = false)
        )

        // Setup mocks
        setupPaginatedConsentMocks(airwallexSpy, pages)
        setupPaymentMethodMocks(airwallexSpy)

        // Execute
        val result = airwallexSpy.fetchAvailablePaymentMethodsAndConsents(mockPaymentSession)

        // Verify - should have retrieved all items from all three pages
        assertEquals(true, result.isSuccess)
        val (_, retrievedConsents) = result.getOrThrow()
        assertEquals(4, retrievedConsents.size) // 1 + 2 + 1 = 4 total

        // Verify retrieveAvailablePaymentConsents was called three times
        coVerify(exactly = 3) {
            airwallexSpy.retrieveAvailablePaymentConsents(any())
        }
    }

    @Test
    fun `pagination logic increments pageNum correctly`() = runTest {
        // Create spy to test pagination
        val airwallexSpy = spyk(airwallex)

        // Setup session with customerId
        every { mockPaymentSession.customerId } returns testCustomerId

        // Track the page numbers being requested
        val requestedPageNums = mutableListOf<Int>()

        // Create responses for two pages
        val page1Response = mockk<Page<PaymentConsent>> {
            every { items } returns listOf(
                mockk {
                    every { paymentMethod } returns mockk {
                        every { type } returns PaymentMethodType.CARD.value
                    }
                }
            )
            every { hasMore } returns true
        }

        val page2Response = mockk<Page<PaymentConsent>> {
            every { items } returns listOf(
                mockk {
                    every { paymentMethod } returns mockk {
                        every { type } returns PaymentMethodType.CARD.value
                    }
                }
            )
            every { hasMore } returns false
        }

        // Mock retrieveAvailablePaymentConsents and capture pageNum
        coEvery {
            airwallexSpy.retrieveAvailablePaymentConsents(any())
        } answers {
            val params = firstArg<RetrieveAvailablePaymentConsentsParams>()
            requestedPageNums.add(params.pageNum)
            if (params.pageNum == 0) page1Response else page2Response
        }

        // Mock payment methods
        val cardMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
        }
        coEvery {
            airwallexSpy["retrieveAvailablePaymentMethodsPaged"](mockPaymentSession, testPaymentIntent.clientSecret)
        } returns listOf(cardMethod)

        // Execute
        airwallexSpy.fetchAvailablePaymentMethodsAndConsents(mockPaymentSession)

        // Verify - pageNum should increment from 0 to 1
        assertEquals(listOf(0, 1), requestedPageNums)
    }

    @Test
    fun `pagination logic uses correct parameters for consent retrieval`() = runTest {
        // Create spy to test pagination
        val airwallexSpy = spyk(airwallex)

        // Setup session with customerId
        every { mockPaymentSession.customerId } returns testCustomerId

        // Capture the parameters passed to retrieveAvailablePaymentConsents
        val capturedParams = slot<RetrieveAvailablePaymentConsentsParams>()

        // Create single page response
        val singlePageResponse = mockk<Page<PaymentConsent>> {
            every { items } returns emptyList()
            every { hasMore } returns false
        }

        // Mock retrieveAvailablePaymentConsents
        coEvery {
            airwallexSpy.retrieveAvailablePaymentConsents(capture(capturedParams))
        } returns singlePageResponse

        // Mock payment methods
        val cardMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
        }
        coEvery {
            airwallexSpy["retrieveAvailablePaymentMethodsPaged"](mockPaymentSession, testPaymentIntent.clientSecret)
        } returns listOf(cardMethod)

        // Execute
        airwallexSpy.fetchAvailablePaymentMethodsAndConsents(mockPaymentSession)

        // Verify parameters
        assertEquals(testPaymentIntent.clientSecret, capturedParams.captured.clientSecret)
        assertEquals(testCustomerId, capturedParams.captured.customerId)
        assertEquals(0, capturedParams.captured.pageNum) // First page
        assertEquals(PaymentConsent.NextTriggeredBy.CUSTOMER, capturedParams.captured.nextTriggeredBy)
        assertEquals(PaymentConsent.PaymentConsentStatus.VERIFIED, capturedParams.captured.status)
    }

    // Helper methods for pagination tests
    private fun createCardConsents(count: Int): List<PaymentConsent> {
        return List(count) {
            mockk {
                every { paymentMethod } returns mockk {
                    every { type } returns PaymentMethodType.CARD.value
                }
            }
        }
    }

    private fun createConsentPage(items: List<PaymentConsent>, hasMore: Boolean): Page<PaymentConsent> {
        return mockk {
            every { this@mockk.items } returns items
            every { this@mockk.hasMore } returns hasMore
        }
    }

    private fun setupPaginatedConsentMocks(spy: Airwallex, pages: List<Page<PaymentConsent>>) {
        pages.forEachIndexed { index, page ->
            coEvery {
                spy.retrieveAvailablePaymentConsents(match { it.pageNum == index })
            } returns page
        }
    }

    private fun setupPaymentMethodMocks(spy: Airwallex) {
        val cardMethod = mockk<AvailablePaymentMethodType> {
            every { name } returns PaymentMethodType.CARD.value
        }
        coEvery {
            spy["retrieveAvailablePaymentMethodsPaged"](mockPaymentSession, testPaymentIntent.clientSecret)
        } returns listOf(cardMethod)
    }

    @Test
    fun `createPaymentMethod with listener calls paymentManager startOperation`() {
        val params = mockk<com.airwallex.android.core.model.CreatePaymentMethodParams> {
            every { clientSecret } returns testClientSecret
            every { customerId } returns testCustomerId
            every { card } returns mockk(relaxed = true)
            every { billing } returns mockk(relaxed = true)
        }
        val listener = mockk<Airwallex.PaymentListener<com.airwallex.android.core.model.PaymentMethod>>(relaxed = true)

        airwallex.createPaymentMethod(params, listener)

        coVerify {
            mockPaymentManager.startOperation(any(), eq(listener))
        }
    }

    @Test
    fun `createPaymentMethod suspend version calls paymentManager createPaymentMethod`() = runTest {
        val params = mockk<com.airwallex.android.core.model.CreatePaymentMethodParams> {
            every { clientSecret } returns testClientSecret
            every { customerId } returns testCustomerId
            every { card } returns mockk(relaxed = true)
            every { billing } returns mockk(relaxed = true)
        }

        coEvery {
            mockPaymentManager.createPaymentMethod(any())
        } returns testPaymentMethod

        val result = airwallex.createPaymentMethod(params)

        assertEquals(testPaymentMethod, result)
        coVerify {
            mockPaymentManager.createPaymentMethod(any())
        }
    }

    // Tests for retrievePaymentIntent
    @Test
    fun `retrievePaymentIntent calls paymentManager startOperation`() {
        val params = mockk<com.airwallex.android.core.model.RetrievePaymentIntentParams> {
            every { clientSecret } returns testClientSecret
            every { paymentIntentId } returns testPaymentIntentId
        }
        val listener = mockk<Airwallex.PaymentListener<com.airwallex.android.core.model.PaymentIntent>>(relaxed = true)

        airwallex.retrievePaymentIntent(params, listener)

        coVerify {
            mockPaymentManager.startOperation(any(), eq(listener))
        }
    }

    // Tests for retrieveAvailablePaymentConsents (suspend)
    @Test
    fun `retrieveAvailablePaymentConsents suspend version calls paymentManager`() = runTest {
        val params = mockk<RetrieveAvailablePaymentConsentsParams> {
            every { clientSecret } returns testClientSecret
            every { customerId } returns testCustomerId
            every { merchantTriggerReason } returns null
            every { nextTriggeredBy } returns null
            every { status } returns null
            every { pageNum } returns 0
            every { pageSize } returns 20
        }
        val expectedResult = mockk<Page<PaymentConsent>>(relaxed = true)

        coEvery {
            mockPaymentManager.retrieveAvailablePaymentConsents(any())
        } returns expectedResult

        val result = airwallex.retrieveAvailablePaymentConsents(params)

        assertEquals(expectedResult, result)
        coVerify {
            mockPaymentManager.retrieveAvailablePaymentConsents(any())
        }
    }

    // Tests for verifyPaymentConsent
    @Test
    fun `verifyPaymentConsent with CARD type uses CardVerificationOptions`() {
        val device = mockk<com.airwallex.android.core.model.Device>(relaxed = true)
        val params = mockk<com.airwallex.android.core.model.VerifyPaymentConsentParams> {
            every { paymentMethodType } returns PaymentMethodType.CARD.value
            every { clientSecret } returns testClientSecret
            every { paymentConsentId } returns testPaymentConsentId
            every { amount } returns java.math.BigDecimal("100")
            every { currency } returns testCurrency
            every { cvc } returns testCvc
            every { returnUrl } returns "https://test.com/return"
        }
        val listener = mockk<Airwallex.PaymentResultListener>(relaxed = true)

        airwallex.verifyPaymentConsent(device, params, listener)

        coVerify {
            mockPaymentManager.startOperation(any(), any<Airwallex.PaymentListener<PaymentConsent>>())
        }
    }

    @Test
    fun `verifyPaymentConsent with GOOGLEPAY type uses CardVerificationOptions`() {
        val device = mockk<com.airwallex.android.core.model.Device>(relaxed = true)
        val params = mockk<com.airwallex.android.core.model.VerifyPaymentConsentParams> {
            every { paymentMethodType } returns PaymentMethodType.GOOGLEPAY.value
            every { clientSecret } returns testClientSecret
            every { paymentConsentId } returns testPaymentConsentId
            every { amount } returns java.math.BigDecimal("100")
            every { currency } returns testCurrency
            every { cvc } returns testCvc
            every { returnUrl } returns "https://test.com/return"
        }
        val listener = mockk<Airwallex.PaymentResultListener>(relaxed = true)

        airwallex.verifyPaymentConsent(device, params, listener)

        coVerify {
            mockPaymentManager.startOperation(any(), any<Airwallex.PaymentListener<PaymentConsent>>())
        }
    }

    @Test
    fun `verifyPaymentConsent with other payment type uses ThirdPartVerificationOptions`() {
        val device = mockk<com.airwallex.android.core.model.Device>(relaxed = true)
        val params = mockk<com.airwallex.android.core.model.VerifyPaymentConsentParams> {
            every { paymentMethodType } returns "wechatpay"
            every { clientSecret } returns testClientSecret
            every { paymentConsentId } returns testPaymentConsentId
            every { amount } returns null
            every { currency } returns null
            every { cvc } returns null
            every { returnUrl } returns "https://test.com/return"
        }
        val listener = mockk<Airwallex.PaymentResultListener>(relaxed = true)

        airwallex.verifyPaymentConsent(device, params, listener)

        coVerify {
            mockPaymentManager.startOperation(any(), any<Airwallex.PaymentListener<PaymentConsent>>())
        }
    }

    // Tests for disablePaymentConsent
    @Test
    fun `disablePaymentConsent calls paymentManager startOperation`() {
        val params = mockk<com.airwallex.android.core.model.DisablePaymentConsentParams> {
            every { clientSecret } returns testClientSecret
            every { paymentConsentId } returns testPaymentConsentId
        }
        val listener = mockk<Airwallex.PaymentListener<PaymentConsent>>(relaxed = true)

        airwallex.disablePaymentConsent(params, listener)

        coVerify {
            mockPaymentManager.startOperation(any(), eq(listener))
        }
    }

    // Tests for retrieveBanks
    @Test
    fun `retrieveBanks calls paymentManager startOperation`() {
        val params = mockk<com.airwallex.android.core.model.RetrieveBankParams> {
            every { clientSecret } returns testClientSecret
            every { paymentMethodType } returns "online_banking"
            every { flow } returns null
            every { transactionMode } returns null
            every { countryCode } returns "TH"
            every { openId } returns null
        }
        val listener = mockk<Airwallex.PaymentListener<com.airwallex.android.core.model.BankResponse>>(relaxed = true)

        airwallex.retrieveBanks(params, listener)

        coVerify {
            mockPaymentManager.startOperation(any(), eq(listener))
        }
    }

    // Tests for retrievePaymentMethodTypeInfo
    @Test
    fun `retrievePaymentMethodTypeInfo calls paymentManager startOperation`() {
        val params = mockk<com.airwallex.android.core.model.RetrievePaymentMethodTypeInfoParams> {
            every { clientSecret } returns testClientSecret
            every { paymentMethodType } returns "card"
            every { flow } returns null
            every { transactionMode } returns null
            every { countryCode } returns null
            every { openId } returns null
        }
        val listener = mockk<Airwallex.PaymentListener<com.airwallex.android.core.model.PaymentMethodTypeInfo>>(relaxed = true)

        airwallex.retrievePaymentMethodTypeInfo(params, listener)

        coVerify {
            mockPaymentManager.startOperation(any(), eq(listener))
        }
    }

    // Tests for confirmPaymentIntentWithDevice
    @Test
    fun `confirmPaymentIntentWithDevice with CARD type calls paymentManager startOperation`() {
        val device = mockk<com.airwallex.android.core.model.Device>(relaxed = true)
        val params = mockk<com.airwallex.android.core.model.ConfirmPaymentIntentParams>(relaxed = true) {
            every { paymentMethodType } returns PaymentMethodType.CARD.value
        }
        val listener = mockk<Airwallex.PaymentResultListener>(relaxed = true)

        every { AirwallexPlugins.environment.threeDsReturnUrl() } returns "https://test.com/return"

        airwallex.confirmPaymentIntentWithDevice(device, params, listener)

        coVerify {
            mockPaymentManager.startOperation(any(), any<Airwallex.PaymentListener<com.airwallex.android.core.model.PaymentIntent>>())
        }
    }

    @Test
    fun `confirmPaymentIntentWithDevice with GOOGLEPAY type calls paymentManager startOperation`() {
        val device = mockk<com.airwallex.android.core.model.Device>(relaxed = true)
        val params = mockk<com.airwallex.android.core.model.ConfirmPaymentIntentParams>(relaxed = true) {
            every { paymentMethodType } returns PaymentMethodType.GOOGLEPAY.value
        }
        val listener = mockk<Airwallex.PaymentResultListener>(relaxed = true)

        every { AirwallexPlugins.environment.threeDsReturnUrl() } returns "https://test.com/return"

        airwallex.confirmPaymentIntentWithDevice(device, params, listener)

        coVerify {
            mockPaymentManager.startOperation(any(), any<Airwallex.PaymentListener<com.airwallex.android.core.model.PaymentIntent>>())
        }
    }

    @Test
    fun `confirmPaymentIntentWithDevice with other payment type calls paymentManager startOperation`() {
        val device = mockk<com.airwallex.android.core.model.Device>(relaxed = true)
        val params = mockk<com.airwallex.android.core.model.ConfirmPaymentIntentParams>(relaxed = true) {
            every { paymentMethodType } returns "wechatpay"
        }
        val listener = mockk<Airwallex.PaymentResultListener>(relaxed = true)

        airwallex.confirmPaymentIntentWithDevice(device, params, listener)

        coVerify {
            mockPaymentManager.startOperation(any(), any<Airwallex.PaymentListener<com.airwallex.android.core.model.PaymentIntent>>())
        }
    }

    // Tests for companion object functions
    @Test
    fun `initialize calls all required static initializers`() {
        val configuration = mockk<AirwallexConfiguration> {
            every { supportComponentProviders } returns emptyList()
            every { enableLogging } returns true
            every { saveLogToLocal } returns false
            every { environment } returns mockk {
                every { baseUrl() } returns "https://api.airwallex.com"
                every { riskEnvironment } returns mockk(relaxed = true)
            }
        }

        Airwallex.initialize(mockApplication, configuration)

        coVerify {
            PaymentIntentProviderRepository.initialize(mockApplication)
            AirwallexPlugins.initialize(configuration)
            AirwallexLogger.initialize(mockApplication, true, false)
            AirwallexLogger.debug(any())
            AirwallexRisk.start(any(), any(), any())
        }
    }

    @Test
    fun `initializeComponents initializes all component providers`() {
        val componentProvider1 = mockk<ActionComponentProvider<out ActionComponent>>(relaxed = true)
        val componentProvider2 = mockk<ActionComponentProvider<out ActionComponent>>(relaxed = true)
        val component1 = mockk<ActionComponent>(relaxed = true)
        val component2 = mockk<ActionComponent>(relaxed = true)

        every { componentProvider1.get() } returns component1
        every { componentProvider2.get() } returns component2

        Airwallex.initializeComponents(mockApplication, listOf(componentProvider1, componentProvider2))

        coVerify {
            component1.initialize(mockApplication)
            component2.initialize(mockApplication)
        }
    }
}
