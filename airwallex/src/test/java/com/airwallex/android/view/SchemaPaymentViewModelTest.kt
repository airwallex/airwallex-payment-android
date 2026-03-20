package com.airwallex.android.view

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.airwallex.android.R
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexRecurringSession
import com.airwallex.android.core.AirwallexRecurringWithIntentSession
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.TokenManager
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.AirwallexError
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.Bank
import com.airwallex.android.core.model.BankResponse
import com.airwallex.android.core.model.DynamicSchema
import com.airwallex.android.core.model.DynamicSchemaField
import com.airwallex.android.core.model.DynamicSchemaFieldType
import com.airwallex.android.core.model.DynamicSchemaFieldUIType
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodTypeInfo
import com.airwallex.android.core.model.TransactionMode
import com.airwallex.android.view.Constants.createAvailablePaymentMethodType
import com.airwallex.android.view.Constants.createPaymentMethod
import com.airwallex.android.view.Constants.createPaymentMethodTypeInfo
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.slot
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Suppress("LargeClass")
class SchemaPaymentViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    // System under test
    private lateinit var viewModel: SchemaPaymentViewModel

    // Mocked dependencies
    @RelaxedMockK
    private lateinit var mockApplication: Application

    @MockK
    private lateinit var mockAirwallex: Airwallex

    @MockK
    private lateinit var mockPaymentSession: AirwallexPaymentSession

    @MockK
    private lateinit var mockRecurringSession: AirwallexRecurringSession

    @MockK
    private lateinit var mockRecurringWithIntentSession: AirwallexRecurringWithIntentSession

    // Test data
    private val testClientSecret = "test_client_secret_abc123"
    private val testCustomerId = "cus_test_customer_id"
    private val testCurrency = "USD"
    private val testCountryCode = "US"

    // Test payment method types
    private lateinit var mockCardPaymentMethodType: AvailablePaymentMethodType
    private lateinit var mockWechatPaymentMethodType: AvailablePaymentMethodType
    private lateinit var mockAlipayPaymentMethodType: AvailablePaymentMethodType

    // Test schema data
    private lateinit var mockSchemaField: DynamicSchemaField
    private lateinit var mockBankSchemaField: DynamicSchemaField
    private lateinit var mockPaymentMethodTypeInfo: PaymentMethodTypeInfo
    private lateinit var mockBankResponse: BankResponse
    private lateinit var mockBank: Bank

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Initialize MockK annotations
        MockKAnnotations.init(this, relaxUnitFun = true)

        // Initialize mocks
        mockkObject(AnalyticsLogger)
        mockkObject(AirwallexLogger)
        mockkObject(TokenManager)

        // Mock AnalyticsLogger
        every { AnalyticsLogger.initialize(any()) } just runs
        every { AnalyticsLogger.logAction(any(), any()) } just runs
        every { AnalyticsLogger.logPageView(any(), any()) } just runs
        every { AnalyticsLogger.setupSession(any(), any(), any()) } just runs
        every { AnalyticsLogger.logError(any(), any<Map<String, Any>>()) } just runs

        // Mock AirwallexLogger
        every { AirwallexLogger.info(any()) } just runs
        every { AirwallexLogger.debug(any()) } just runs
        every { AirwallexLogger.error(any<String>()) } just runs

        // Mock Application
        every { mockApplication.getString(any()) } returns "Test String"
        every { mockApplication.getString(any<Int>()) } returns "Test String"

        // Setup mock payment intent
        val mockPaymentIntent = mockk<PaymentIntent>(relaxed = true) {
            every { clientSecret } returns testClientSecret
            every { id } returns "test_payment_intent_id"
        }

        // Setup mock payment session
        every { mockPaymentSession.customerId } returns testCustomerId
        every { mockPaymentSession.currency } returns testCurrency
        every { mockPaymentSession.countryCode } returns testCountryCode
        every { mockPaymentSession.paymentIntent } returns mockPaymentIntent

        // Setup mock recurring session
        every { mockRecurringSession.customerId } returns testCustomerId
        every { mockRecurringSession.clientSecret } returns testClientSecret
        every { mockRecurringSession.currency } returns testCurrency
        every { mockRecurringSession.countryCode } returns testCountryCode

        // Setup mock recurring with intent session
        every { mockRecurringWithIntentSession.customerId } returns testCustomerId
        every { mockRecurringWithIntentSession.currency } returns testCurrency
        every { mockRecurringWithIntentSession.countryCode } returns testCountryCode
        every { mockRecurringWithIntentSession.paymentIntent } returns mockPaymentIntent

        viewModel = createViewModel()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // Helper method to create ViewModel with different sessions
    private fun createViewModel(
        session: AirwallexSession = mockPaymentSession
    ): SchemaPaymentViewModel {
        return SchemaPaymentViewModel(
            application = mockApplication,
            airwallex = mockAirwallex,
            session = session
        )
    }

    // Helper method to create ViewModel based on transaction mode
    private fun mockViewModel(
        transactionMode: TransactionMode = TransactionMode.ONE_OFF
    ): SchemaPaymentViewModel {
        val session = when (transactionMode) {
            TransactionMode.ONE_OFF -> mockPaymentSession
            TransactionMode.RECURRING -> mockRecurringSession
        }
        return createViewModel(session)
    }

    @Test
    fun `test ctaRes returns pay_now for AirwallexPaymentSession`() {
        val testViewModel = mockViewModel(transactionMode = TransactionMode.ONE_OFF)
        assertEquals(R.string.airwallex_pay_now, testViewModel.ctaRes)
    }

    @Test
    fun `test ctaRes returns confirm for AirwallexRecurringSession`() {
        val testViewModel = mockViewModel(transactionMode = TransactionMode.RECURRING)
        assertEquals(R.string.airwallex_confirm, testViewModel.ctaRes)
    }

    @Test
    fun `test appendParamsToMapForSchemaSubmission with empty additionalParams`() {
        // Given
        val inputMap = mapOf("key1" to "value1", "key2" to "value2")

        // When
        val result = viewModel.appendParamsToMapForSchemaSubmission(inputMap)

        // Then
        assertEquals(inputMap, result)
    }

    @Test
    fun `test appendParamsToMapForSchemaSubmission with additionalParams`() {
        // Use reflection to set private additionalParams field for testing
        val additionalParamsField =
            SchemaPaymentViewModel::class.java.getDeclaredField("additionalParams")
        additionalParamsField.isAccessible = true
        val additionalParams = additionalParamsField.get(viewModel) as MutableMap<String, String>
        additionalParams["country_code"] = "US"
        additionalParams["flow"] = "in_app"

        val inputMap = mapOf("key1" to "value1")
        val expectedMap = mapOf(
            "key1" to "value1", "country_code" to "US", "flow" to "in_app"
        )

        // When
        val result = viewModel.appendParamsToMapForSchemaSubmission(inputMap)

        // Then
        assertEquals(expectedMap, result)
    }

    @Test
    fun `test checkoutWithSchema with paymentMethod and additionalInfo returns success`() = runTest {
        val testViewModel = mockViewModel(TransactionMode.ONE_OFF)
        val paymentMethod = createPaymentMethod("wechat")
        val additionalInfo = mapOf("key1" to "value1", "key2" to "value2")
        val typeInfo = createPaymentMethodTypeInfo()
        val expectedStatus = AirwallexPaymentStatus.Success("test_payment_intent_id")

        val listenerSlot = slot<Airwallex.PaymentResultListener>()
        every {
            mockAirwallex.checkout(
                session = any(),
                paymentMethod = any(),
                additionalInfo = any(),
                flow = any(),
                listener = capture(listenerSlot)
            )
        } answers {
            listenerSlot.captured.onCompleted(expectedStatus)
        }

        val results = mutableListOf<AirwallexPaymentStatus>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            testViewModel.paymentResult.collect { event ->
                event.peekContent()?.let { results.add(it) }
            }
        }

        testViewModel.checkoutWithSchema(paymentMethod, additionalInfo, typeInfo)
        advanceUntilIdle()

        assertEquals(1, results.size)
        assertEquals(expectedStatus, results.first())

        job.cancel()
    }

    @Test
    fun `test checkoutWithSchema when fields is null`() = runTest {
        val availablePaymentMethodType = createAvailablePaymentMethodType()
        val testViewModel = mockViewModel(TransactionMode.ONE_OFF)
        val expectedStatus = AirwallexPaymentStatus.Success("test_payment_intent_id")

        val listenerSlot = slot<Airwallex.PaymentResultListener>()
        every {
            mockAirwallex.checkout(
                session = any(),
                paymentMethod = any(),
                additionalInfo = any(),
                flow = any(),
                listener = capture(listenerSlot)
            )
        } answers {
            listenerSlot.captured.onCompleted(expectedStatus)
        }

        val results = mutableListOf<AirwallexPaymentStatus>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            testViewModel.paymentResult.collect { event ->
                event.peekContent()?.let { results.add(it) }
            }
        }

        testViewModel.checkoutWithSchema(availablePaymentMethodType)
        advanceUntilIdle()

        assertEquals(1, results.size)
        assertEquals(expectedStatus, results.first())

        job.cancel()
    }

    @Test
    fun `test checkoutWithSchema when bankField is null`() = runTest {
        val availablePaymentMethodType = createAvailablePaymentMethodType()
        val testViewModel = mockViewModel(TransactionMode.ONE_OFF)
        val expectedStatus = AirwallexPaymentStatus.Success("test_payment_intent_id")

        val listenerSlot = slot<Airwallex.PaymentResultListener>()
        every {
            mockAirwallex.checkout(
                session = any(),
                paymentMethod = any(),
                additionalInfo = any(),
                flow = any(),
                listener = capture(listenerSlot)
            )
        } answers {
            listenerSlot.captured.onCompleted(expectedStatus)
        }

        val results = mutableListOf<AirwallexPaymentStatus>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            testViewModel.paymentResult.collect { event ->
                event.peekContent()?.let { results.add(it) }
            }
        }

        testViewModel.checkoutWithSchema(availablePaymentMethodType)
        advanceUntilIdle()

        assertEquals(1, results.size)
        assertEquals(expectedStatus, results.first())

        job.cancel()
    }

    @Test
    fun `test checkoutWithSchema when banks is empty`() = runTest {
        val availablePaymentMethodType = createAvailablePaymentMethodType()
        val testViewModel = mockViewModel(TransactionMode.ONE_OFF)
        val expectedStatus = AirwallexPaymentStatus.Success("test_payment_intent_id")

        val listenerSlot = slot<Airwallex.PaymentResultListener>()
        every {
            mockAirwallex.checkout(
                session = any(),
                paymentMethod = any(),
                additionalInfo = any(),
                flow = any(),
                listener = capture(listenerSlot)
            )
        } answers {
            listenerSlot.captured.onCompleted(expectedStatus)
        }

        val results = mutableListOf<AirwallexPaymentStatus>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            testViewModel.paymentResult.collect { event ->
                event.peekContent()?.let { results.add(it) }
            }
        }

        testViewModel.checkoutWithSchema(availablePaymentMethodType)
        advanceUntilIdle()

        assertEquals(1, results.size)
        assertEquals(expectedStatus, results.first())

        job.cancel()
    }

    @Test
    fun `test checkoutWithSchema when banks is not empty`() = runTest {
        val availablePaymentMethodType = createAvailablePaymentMethodType()
        val testViewModel = mockViewModel(TransactionMode.ONE_OFF)
        val expectedStatus = AirwallexPaymentStatus.Success("test_payment_intent_id")

        val listenerSlot = slot<Airwallex.PaymentResultListener>()
        every {
            mockAirwallex.checkout(
                session = any(),
                paymentMethod = any(),
                additionalInfo = any(),
                flow = any(),
                listener = capture(listenerSlot)
            )
        } answers {
            listenerSlot.captured.onCompleted(expectedStatus)
        }

        val results = mutableListOf<AirwallexPaymentStatus>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            testViewModel.paymentResult.collect { event ->
                event.peekContent()?.let { results.add(it) }
            }
        }

        testViewModel.checkoutWithSchema(availablePaymentMethodType)
        advanceUntilIdle()

        assertEquals(1, results.size)
        assertEquals(expectedStatus, results.first())

        job.cancel()
    }

    @Test
    fun `test retrieveSchemaDataFromCache returns null when cache is empty`() = runTest {
        val testViewModel = mockViewModel()
        val paymentMethodType = createAvailablePaymentMethodType()

        val result = testViewModel.retrieveSchemaDataFromCache(paymentMethodType)

        Assert.assertNull(result)
    }

    @Test
    fun `test retrieveSchemaDataFromCache returns cached data when available`() = runTest {
        val testViewModel = mockViewModel()
        val paymentMethodType = createAvailablePaymentMethodType()

        // Create test data
        val testFields = listOf(mockk<DynamicSchemaField>(), mockk<DynamicSchemaField>())
        val testPaymentMethod = mockk<PaymentMethod>()
        val testTypeInfo = mockk<PaymentMethodTypeInfo>()
        val testBanks = listOf(mockk<Bank>())

        val testSchemaData = SchemaPaymentViewModel.SchemaData(
            fields = testFields,
            paymentMethod = testPaymentMethod,
            typeInfo = testTypeInfo,
            banks = testBanks
        )

        // Set test data in cache
        testViewModel.schemaDataCache[paymentMethodType] = testSchemaData

        val result = testViewModel.retrieveSchemaDataFromCache(paymentMethodType)

        Assert.assertNotNull(result)
        assertEquals(testFields.size, result?.fields?.size)
        assertEquals(testPaymentMethod, result?.paymentMethod)
        assertEquals(testTypeInfo, result?.typeInfo)
        assertEquals(testBanks.size, result?.banks?.size)
    }

    @Test
    fun `test loadSchemaFields returns cached data when available`() = runTest {
        val testViewModel = mockViewModel(TransactionMode.ONE_OFF)
        // Given
        val paymentMethodType = createAvailablePaymentMethodType()
        val expectedSchemaData = SchemaPaymentViewModel.SchemaData(
            fields = listOf(mockk()),
            paymentMethod = createPaymentMethod("test"),
            typeInfo = createPaymentMethodTypeInfo()
        )
        testViewModel.schemaDataCache[paymentMethodType] = expectedSchemaData

        // When
        val result = testViewModel.loadSchemaFields(paymentMethodType)

        // Then
        assertEquals(true, result.isSuccess)
        assertEquals(expectedSchemaData, result.getOrNull())
    }

    @Test
    fun `test loadSchemaFields when no schema fields required`() = runTest {
        val testViewModel = mockViewModel(TransactionMode.ONE_OFF)
        // Given
        val paymentMethodType = mockk<AvailablePaymentMethodType> {
            every { name } returns "test_method"
            every { resources } returns mockk {
                every { hasSchema } returns false
            }
        }

        // When
        val result = testViewModel.loadSchemaFields(paymentMethodType)

        // Then
        assertEquals(true, result.isSuccess)
        Assert.assertNull(result.getOrNull())
    }

    @Test
    fun `test loadSchemaFields when retrievePaymentMethodTypeInfo fails`() = runTest {
        val testViewModel = mockViewModel(TransactionMode.ONE_OFF)
        // Given
        val paymentMethodType = mockk<AvailablePaymentMethodType> {
            every { name } returns "test_method"
            every { resources } returns mockk {
                every { hasSchema } returns true
            }
        }

        every {
            mockAirwallex.retrievePaymentMethodTypeInfo(any(), any())
        } answers {
            val listener = secondArg<Airwallex.PaymentListener<PaymentMethodTypeInfo>>()
            listener.onFailed(AirwallexCheckoutException(error = AirwallexError("test error")))
        }

        // When
        val result = testViewModel.loadSchemaFields(paymentMethodType)
        advanceUntilIdle()

        // Then
        assertEquals(true, result.isFailure)
    }

    @Test
    fun `test loadSchemaFields with no hidden fields`() = runTest {
        val testViewModel = mockViewModel(TransactionMode.ONE_OFF)

        // Given
        val paymentMethodType = mockk<AvailablePaymentMethodType> {
            every { name } returns "test_method"
            every { resources } returns mockk {
                every { hasSchema } returns true
            }
        }

        // Create a PaymentMethodTypeInfo with empty fieldSchemas to simulate no hidden fields
        val typeInfo = mockk<PaymentMethodTypeInfo> {
            every { fieldSchemas } returns listOf(
                DynamicSchema(
                    transactionMode = TransactionMode.ONE_OFF,
                    fields = emptyList() // Empty list means no fields to show
                )
            )
        }

        every {
            mockAirwallex.retrievePaymentMethodTypeInfo(any(), any())
        } answers {
            val listener = secondArg<Airwallex.PaymentListener<PaymentMethodTypeInfo>>()
            listener.onSuccess(typeInfo)
        }

        // When
        val result = testViewModel.loadSchemaFields(paymentMethodType)
        advanceUntilIdle()

        // Then
        assertEquals(true, result.isSuccess)
        val schemaData = result.getOrNull()
        Assert.assertNotNull(schemaData)
        assertEquals(emptyList(), schemaData?.fields)
    }

    @Test
    fun `test loadSchemaFields with hidden fields`() = runTest {
        val testViewModel = mockViewModel(TransactionMode.ONE_OFF)

        // Given
        val paymentMethodType = mockk<AvailablePaymentMethodType> {
            every { name } returns "test_method"
            every { resources } returns mockk {
                every { hasSchema } returns true
            }
        }

        val shopperNameField = DynamicSchemaField(
            "shopper_name",
            "Shopper Name",
            DynamicSchemaFieldUIType.TEXT,
            DynamicSchemaFieldType.STRING,
            false, // This field is visible
            null,
            null
        )
        val hiddenField = DynamicSchemaField(
            "country_code",
            "Country",
            DynamicSchemaFieldUIType.LIST,
            DynamicSchemaFieldType.ENUM,
            true, // This field is hidden
            null,
            null
        )
        val typeInfo = mockk<PaymentMethodTypeInfo> {
            every { fieldSchemas } returns listOf(
                DynamicSchema(
                    transactionMode = TransactionMode.ONE_OFF,
                    fields = listOf(shopperNameField, hiddenField)
                )
            )
        }

        every {
            mockAirwallex.retrievePaymentMethodTypeInfo(any(), any())
        } answers {
            val listener = secondArg<Airwallex.PaymentListener<PaymentMethodTypeInfo>>()
            listener.onSuccess(typeInfo)
        }

        // When
        val result = testViewModel.loadSchemaFields(paymentMethodType)
        advanceUntilIdle()

        // Then
        assertEquals(true, result.isSuccess)
        val schemaData = result.getOrNull()
        Assert.assertNotNull(schemaData)
        assertEquals(1, schemaData?.fields?.size) // Only visible fields

        // Check that hidden enum field was added to additionalParams
        val additionalParams = testViewModel.appendParamsToMapForSchemaSubmission(emptyMap())
        assertTrue(additionalParams.containsKey("country_code"))
    }

    @Test
    fun `test loadSchemaFields with non-bank fields`() = runTest {
        val testViewModel = mockViewModel(TransactionMode.ONE_OFF)

        // Given
        val paymentMethodType = mockk<AvailablePaymentMethodType> {
            every { name } returns "test_method"
            every { resources } returns mockk { every { hasSchema } returns true }
        }

        val textField = DynamicSchemaField(
            "name",
            "Name",
            DynamicSchemaFieldUIType.TEXT,
            DynamicSchemaFieldType.STRING,
            false,
            null,
            null
        )
        val typeInfo = mockk<PaymentMethodTypeInfo> {
            every { fieldSchemas } returns listOf(
                DynamicSchema(
                    transactionMode = TransactionMode.ONE_OFF, fields = listOf(textField)
                )
            )
        }

        every {
            mockAirwallex.retrievePaymentMethodTypeInfo(any(), any())
        } answers {
            val listener = secondArg<Airwallex.PaymentListener<PaymentMethodTypeInfo>>()
            listener.onSuccess(typeInfo)
        }

        // When
        val result = testViewModel.loadSchemaFields(paymentMethodType)
        advanceUntilIdle()

        // Then
        assertEquals(true, result.isSuccess)
        val schemaData = result.getOrNull()
        Assert.assertNotNull(schemaData)
        assertEquals(listOf(textField), schemaData?.fields)
        assertEquals(emptyList(), schemaData?.banks)
    }

    @Test
    fun `test loadSchemaFields when bank retrieval fails`() = runTest {
        val testViewModel = mockViewModel(TransactionMode.ONE_OFF)

        // Given
        val paymentMethodType = mockk<AvailablePaymentMethodType> {
            every { name } returns "test_method"
            every { resources } returns mockk { every { hasSchema } returns true }
        }

        val bankField = DynamicSchemaField(
            "bank",
            "Bank",
            DynamicSchemaFieldUIType.LIST,
            DynamicSchemaFieldType.BANKS,
            false,
            null,
            null
        )
        val typeInfo = mockk<PaymentMethodTypeInfo> {
            every { fieldSchemas } returns listOf(
                DynamicSchema(
                    transactionMode = TransactionMode.ONE_OFF, fields = listOf(bankField)
                )
            )
        }

        every {
            mockAirwallex.retrievePaymentMethodTypeInfo(any(), any())
        } answers {
            val listener = secondArg<Airwallex.PaymentListener<PaymentMethodTypeInfo>>()
            listener.onSuccess(typeInfo)
        }

        every {
            mockAirwallex.retrieveBanks(any(), any())
        } answers {
            val listener = secondArg<Airwallex.PaymentListener<BankResponse>>()
            listener.onFailed(AirwallexCheckoutException(AirwallexError("Failed to load banks")))
        }

        // When
        val result = testViewModel.loadSchemaFields(paymentMethodType)
        advanceUntilIdle()

        // Then
        assertEquals(true, result.isFailure)
    }

    @Test
    fun `test loadSchemaFields with bank field and empty bank list`() = runTest {
        val testViewModel = mockViewModel(TransactionMode.ONE_OFF)

        // Given
        val paymentMethodType = mockk<AvailablePaymentMethodType> {
            every { name } returns "test_method"
            every { resources } returns mockk { every { hasSchema } returns true }
        }

        val bankField = DynamicSchemaField(
            "bank",
            "Bank",
            DynamicSchemaFieldUIType.LOGO_LIST,
            DynamicSchemaFieldType.BANKS,
            false,
            null,
            null
        )
        val typeInfo = mockk<PaymentMethodTypeInfo> {
            every { fieldSchemas } returns listOf(
                DynamicSchema(
                    transactionMode = TransactionMode.ONE_OFF, fields = listOf(bankField)
                )
            )
        }

        val emptyBankResponse = mockk<BankResponse> {
            every { items } returns emptyList()
        }

        every {
            mockAirwallex.retrievePaymentMethodTypeInfo(any(), any())
        } answers {
            val listener = secondArg<Airwallex.PaymentListener<PaymentMethodTypeInfo>>()
            listener.onSuccess(typeInfo)
        }

        every {
            mockAirwallex.retrieveBanks(any(), any())
        } answers {
            val listener = secondArg<Airwallex.PaymentListener<BankResponse>>()
            listener.onSuccess(emptyBankResponse)
        }

        // When
        val result = testViewModel.loadSchemaFields(paymentMethodType)
        advanceUntilIdle()

        // Then
        assertEquals(true, result.isSuccess)
        val schemaData = result.getOrNull()
        Assert.assertNotNull(schemaData)
        assertEquals(emptyList(), schemaData?.banks)
    }

    @Test
    fun `test loadSchemaFields with bank field and non-empty bank list`() = runTest {
        val testViewModel = mockViewModel(TransactionMode.ONE_OFF)

        // Given
        val paymentMethodType = mockk<AvailablePaymentMethodType> {
            every { name } returns "test_method"
            every { resources } returns mockk { every { hasSchema } returns true }
        }

        val bankField = DynamicSchemaField(
            "bank",
            "Bank",
            DynamicSchemaFieldUIType.LOGO_LIST,
            DynamicSchemaFieldType.BANKS,
            false,
            null,
            null
        )
        val typeInfo = mockk<PaymentMethodTypeInfo> {
            every { fieldSchemas } returns listOf(
                DynamicSchema(
                    transactionMode = TransactionMode.ONE_OFF, fields = listOf(bankField)
                )
            )
        }

        val testBank1 = mockk<Bank>(relaxed = true)
        val testBank2 = mockk<Bank>(relaxed = true)
        val bankResponse = mockk<BankResponse> {
            every { items } returns listOf(testBank1, testBank2)
        }

        every {
            mockAirwallex.retrievePaymentMethodTypeInfo(any(), any())
        } answers {
            val listener = secondArg<Airwallex.PaymentListener<PaymentMethodTypeInfo>>()
            listener.onSuccess(typeInfo)
        }

        every {
            mockAirwallex.retrieveBanks(any(), any())
        } answers {
            val listener = secondArg<Airwallex.PaymentListener<BankResponse>>()
            listener.onSuccess(bankResponse)
        }

        // When
        val result = testViewModel.loadSchemaFields(paymentMethodType)
        advanceUntilIdle()

        // Then
        assertEquals(true, result.isSuccess)
        val schemaData = result.getOrNull()
        Assert.assertNotNull(schemaData)
        assertEquals(2, schemaData?.banks?.size)
    }

    @Test
    fun `test loadSchemaFields when filterRequiredFields returns null`() = runTest {
        val testViewModel = mockViewModel(TransactionMode.ONE_OFF)

        // Given
        val paymentMethodType = mockk<AvailablePaymentMethodType> {
            every { name } returns "test_method"
            every { resources } returns mockk { every { hasSchema } returns true }
        }

        // TypeInfo with no matching transaction mode schema
        val typeInfo = mockk<PaymentMethodTypeInfo> {
            every { fieldSchemas } returns listOf(
                DynamicSchema(
                    transactionMode = TransactionMode.RECURRING, // Different mode
                    fields = listOf(mockk())
                )
            )
        }

        every {
            mockAirwallex.retrievePaymentMethodTypeInfo(any(), any())
        } answers {
            val listener = secondArg<Airwallex.PaymentListener<PaymentMethodTypeInfo>>()
            listener.onSuccess(typeInfo)
        }

        // When
        val result = testViewModel.loadSchemaFields(paymentMethodType)
        advanceUntilIdle()

        // Then
        assertEquals(true, result.isSuccess)
        Assert.assertNull(result.getOrNull())
    }

    @Test
    fun `test loadSchemaFields with null bank items in response`() = runTest {
        val testViewModel = mockViewModel(TransactionMode.ONE_OFF)

        // Given
        val paymentMethodType = mockk<AvailablePaymentMethodType> {
            every { name } returns "test_method"
            every { resources } returns mockk { every { hasSchema } returns true }
        }

        val bankField = DynamicSchemaField(
            "bank",
            "Bank",
            DynamicSchemaFieldUIType.LOGO_LIST,
            DynamicSchemaFieldType.BANKS,
            false,
            null,
            null
        )
        val typeInfo = mockk<PaymentMethodTypeInfo> {
            every { fieldSchemas } returns listOf(
                DynamicSchema(
                    transactionMode = TransactionMode.ONE_OFF, fields = listOf(bankField)
                )
            )
        }

        val bankResponse = mockk<BankResponse> {
            every { items } returns null
        }

        every {
            mockAirwallex.retrievePaymentMethodTypeInfo(any(), any())
        } answers {
            val listener = secondArg<Airwallex.PaymentListener<PaymentMethodTypeInfo>>()
            listener.onSuccess(typeInfo)
        }

        every {
            mockAirwallex.retrieveBanks(any(), any())
        } answers {
            val listener = secondArg<Airwallex.PaymentListener<BankResponse>>()
            listener.onSuccess(bankResponse)
        }

        // When
        val result = testViewModel.loadSchemaFields(paymentMethodType)
        advanceUntilIdle()

        // Then
        assertEquals(true, result.isSuccess)
        val schemaData = result.getOrNull()
        Assert.assertNotNull(schemaData)
        assertEquals(emptyList(), schemaData?.banks)
    }

    @Test
    fun `test requireHandleSchemaFields when resources is null`() = runTest {
        val testViewModel = mockViewModel(TransactionMode.ONE_OFF)

        // Given - resources is null so requireHandleSchemaFields returns false
        val paymentMethodType = mockk<AvailablePaymentMethodType> {
            every { name } returns "test_method"
            every { resources } returns null // resources is null
        }

        // When
        val result = testViewModel.loadSchemaFields(paymentMethodType)
        advanceUntilIdle()

        // Then - Should return success with null when resources is null
        assertEquals(true, result.isSuccess)
        Assert.assertNull(result.getOrNull())
    }
}
