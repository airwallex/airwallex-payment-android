package com.airwallex.android.view

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexRecurringSession
import com.airwallex.android.core.GooglePayOptions
import com.airwallex.android.core.TokenManager
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.BankResponse
import com.airwallex.android.core.model.Page
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.core.model.PaymentMethodTypeInfo
import com.airwallex.android.core.model.TransactionMode
import com.airwallex.android.view.Constants.SAMPLE_BANK_FIELD
import com.airwallex.android.view.Constants.SAMPLE_BANK_RESPONSE
import com.airwallex.android.view.Constants.SAMPLE_ENUM_FIELD
import com.airwallex.android.view.Constants.createAvailablePaymentMethodType
import com.airwallex.android.view.Constants.createBankResponse
import com.airwallex.android.view.Constants.createPaymentConsents
import com.airwallex.android.view.Constants.createPaymentMethod
import com.airwallex.android.view.Constants.createPaymentMethodTypeInfo
import com.airwallex.android.view.Constants.createPaymentMethods
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.slot
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.unmockkObject
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

@Suppress("LargeClass")
class PaymentMethodsViewModelTest {
    private lateinit var airwallex: Airwallex
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkObject(AnalyticsLogger)
        mockkObject(AirwallexLogger)
    }

    @After
    fun unmock() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
        unmockkAll()
    }

    @Test
    fun `test deletePaymentConsent returns success response`() = runBlocking {
        val paymentConsent = mockk<PaymentConsent> {
            every { id } returns "consent_id"
        }
        val viewModel = mockViewModel(TransactionMode.ONE_OFF)
        val resultData = MutableLiveData<Result<PaymentConsent>>()
        val slot = slot<Airwallex.PaymentListener<PaymentConsent>>()
        every {
            airwallex.disablePaymentConsent(
                any(),
                capture(slot)
            )
        } answers {
            slot.captured.onSuccess(paymentConsent)
        }
        viewModel.deletePaymentConsent(paymentConsent).observeForever {
            resultData.value = it
        }
        assertEquals(Result.success(paymentConsent), resultData.value)
    }

    @Test
    fun `test deletePaymentConsent returns failure response when clientSecret is null`() =
        runBlocking {
            val viewModel = mockViewModel(TransactionMode.ONE_OFF)
            val paymentConsent = mockk<PaymentConsent> {
                every { id } returns "consent_id"
            }
            val observer = mockk<Observer<Result<PaymentConsent>>>(relaxed = true)
            coEvery {
                airwallex.disablePaymentConsent(any(), any())
            } throws AirwallexCheckoutException(message = "Client secret is missing")
            val liveData = viewModel.deletePaymentConsent(paymentConsent)
            liveData.observeForever(observer)
            verify {
                observer.onChanged(
                    withArg { result ->
                        assertTrue(result.isFailure)
                        assertTrue(result.exceptionOrNull() is AirwallexCheckoutException)
                    }
                )
            }
        }

    @Test
    fun `test deletePaymentConsent calls onFailed on exception`() = runBlocking {
        val viewModel = mockViewModel(TransactionMode.ONE_OFF)
        val paymentConsent = mockk<PaymentConsent> {
            every { id } returns "consent_id"
        }
        val observer = mockk<Observer<Result<PaymentConsent>>>(relaxed = true)
        val slot = slot<Airwallex.PaymentListener<PaymentConsent>>()
        every {
            airwallex.disablePaymentConsent(any(), capture(slot))
        } answers {
            slot.captured.onFailed(AirwallexCheckoutException(message = "Exception occurred"))
        }
        val liveData = viewModel.deletePaymentConsent(paymentConsent)
        liveData.observeForever(observer)
        verify {
            observer.onChanged(
                withArg { result ->
                    assertTrue(result.isFailure)
                    assertTrue(result.exceptionOrNull() is AirwallexCheckoutException)
                }
            )
        }
    }

    @Test
    fun `test confirmPaymentIntent with invalid session`() {
        val viewModel = mockViewModel(TransactionMode.RECURRING)

        val paymentConsent = mockk<PaymentConsent>(relaxed = true)
        val observer = mockk<Observer<PaymentMethodsViewModel.PaymentFlowStatus>>(relaxed = true)

        viewModel.paymentFlowStatus.observeForever(observer)

        val expectedFailureStatus = PaymentMethodsViewModel.PaymentFlowStatus.PaymentStatus(
            AirwallexPaymentStatus.Failure(
                AirwallexCheckoutException(message = "confirm with paymentConsent only support AirwallexPaymentSession")
            )
        )

        viewModel.confirmPaymentIntent(paymentConsent)

        verify {
            observer.onChanged(
                match { status ->
                    status is PaymentMethodsViewModel.PaymentFlowStatus.PaymentStatus &&
                            status.status is AirwallexPaymentStatus.Failure &&
                            (status.status).exception.message == (expectedFailureStatus.status as AirwallexPaymentStatus.Failure).exception.message
                }
            )
        }
        viewModel.paymentFlowStatus.removeObserver(observer)
    }

    @Test
    fun `test confirmPaymentIntent with valid session`() {
        val paymentConsent = mockk<PaymentConsent>(relaxed = true)
        val viewModel = mockViewModel(TransactionMode.ONE_OFF)
        val observer = mockk<Observer<PaymentMethodsViewModel.PaymentFlowStatus>>(relaxed = true)

        val status = AirwallexPaymentStatus.Success(
            paymentIntentId = "test_payment_intent_id",
            consentId = "test_consent_id",
            additionalInfo = mapOf("key" to "value")
        )
        every {
            airwallex.confirmPaymentIntent(
                session = any(),
                paymentConsent = any(),
                any()
            )
        } answers {
            thirdArg<Airwallex.PaymentResultListener>().onCompleted(status)
        }

        viewModel.paymentFlowStatus.observeForever(observer)
        viewModel.confirmPaymentIntent(paymentConsent)

        verify {
            observer.onChanged(
                match { result ->
                    result is PaymentMethodsViewModel.PaymentFlowStatus.PaymentStatus &&
                            result.status is AirwallexPaymentStatus.Success &&
                            result.status.paymentIntentId == status.paymentIntentId &&
                            result.status.consentId == status.consentId &&
                            result.status.additionalInfo == status.additionalInfo
                }
            )
        }
        viewModel.paymentFlowStatus.removeObserver(observer)
    }

    @Test
    fun `test startCheckout calls trackPaymentSuccess and updates paymentFlowStatus`() = runTest {
        val paymentMethod = createPaymentMethod("card")
        val additionalInfo = mapOf("key" to "value")
        val paymentMethodTypeInfo = createPaymentMethodTypeInfo()
        val expectedStatus = AirwallexPaymentStatus.Success("test_payment_intent_id")
        val expectedPaymentFlowStatus = PaymentMethodsViewModel.PaymentFlowStatus.PaymentStatus(expectedStatus)

        val viewModel = mockViewModel(TransactionMode.ONE_OFF)
        val listenerSlot = slot<Airwallex.PaymentResultListener>()

        coEvery {
            airwallex.checkout(
                session = any(),
                paymentMethod = any(),
                additionalInfo = any(),
                flow = any(),
                listener = capture(listenerSlot)
            )
        } answers {
            listenerSlot.captured.onCompleted(expectedStatus)
        }

        every { viewModel.trackPaymentSuccess(expectedStatus, paymentMethod.type) } just runs
        val observer = mockk<Observer<PaymentMethodsViewModel.PaymentFlowStatus>>(relaxed = true)
        viewModel.paymentFlowStatus.observeForever(observer)
        viewModel.startCheckout(paymentMethod, additionalInfo, paymentMethodTypeInfo)
        advanceUntilIdle()

        coVerify(exactly = 1) {
            viewModel.trackPaymentSuccess(expectedStatus, paymentMethod.type)
        }
        verify(exactly = 1) { observer.onChanged(expectedPaymentFlowStatus) }
        viewModel.paymentFlowStatus.removeObserver(observer)
    }

    @Test
    fun `test startCheckout invokes checkout directly when requireHandleSchemaFields is true`() =
        runTest {
            val availablePaymentMethodType = createAvailablePaymentMethodType()
            val viewModel = mockViewModel(TransactionMode.ONE_OFF)

            coEvery {
                airwallex.retrievePaymentMethodTypeInfo(any(), any())
            } coAnswers {
                val listener = secondArg<Airwallex.PaymentListener<PaymentMethodTypeInfo>>()
                listener.onFailed(AirwallexCheckoutException(message = "Failure message"))
            }
            every { AirwallexLogger.info(any<String>(), any()) } just Runs

            viewModel.startCheckout(availablePaymentMethodType)
            advanceUntilIdle()
            verify(exactly = 1) { AirwallexLogger.info("PaymentMethodsViewModel get more payment Info fields on one-off flow.") }
            unmockkObject(AirwallexLogger)
        }

    @Test
    fun `test checkoutWithSchemaFields when fields is null`() = runTest {
        val availablePaymentMethodType = createAvailablePaymentMethodType()
        val paymentMethodTypeInfo = createPaymentMethodTypeInfo()
        val viewModel = mockViewModel(TransactionMode.ONE_OFF)
        val expectedStatus = AirwallexPaymentStatus.Success("test_payment_intent_id")
        val expectedPaymentFlowStatus = PaymentMethodsViewModel.PaymentFlowStatus.PaymentStatus(expectedStatus)

        val mockListener = slot<Airwallex.PaymentListener<PaymentMethodTypeInfo>>()

        every {
            airwallex.retrievePaymentMethodTypeInfo(any(), capture(mockListener))
        } answers {
            mockListener.captured.onSuccess(paymentMethodTypeInfo)
        }

        val listenerSlot = slot<Airwallex.PaymentResultListener>()
        coEvery {
            airwallex.checkout(
                session = any(),
                paymentMethod = any(),
                additionalInfo = any(),
                flow = any(),
                listener = capture(listenerSlot)
            )
        } answers {
            listenerSlot.captured.onCompleted(expectedStatus)
        }

        val observer = mockk<Observer<PaymentMethodsViewModel.PaymentFlowStatus>>(relaxed = true)
        viewModel.paymentFlowStatus.observeForever(observer)

        viewModel.startCheckout(availablePaymentMethodType)

        advanceUntilIdle()

        assertEquals(viewModel.paymentFlowStatus.value, expectedPaymentFlowStatus)

        viewModel.paymentFlowStatus.removeObserver(observer)
        unmockkObject(AirwallexLogger)
    }

    @Test
    fun `test checkoutWithSchemaFields when bankField is null`() = runTest {
        val availablePaymentMethodType = createAvailablePaymentMethodType()
        val paymentMethodTypeInfo = createPaymentMethodTypeInfo(SAMPLE_ENUM_FIELD)
        val viewModel = mockViewModel(TransactionMode.ONE_OFF)
        val expectedStatus = AirwallexPaymentStatus.Success("test_payment_intent_id")

        val mockListener = slot<Airwallex.PaymentListener<PaymentMethodTypeInfo>>()

        every {
            airwallex.retrievePaymentMethodTypeInfo(any(), capture(mockListener))
        } answers {
            mockListener.captured.onSuccess(paymentMethodTypeInfo)
        }

        val listenerSlot = slot<Airwallex.PaymentResultListener>()
        coEvery {
            airwallex.checkout(
                session = any(),
                paymentMethod = any(),
                additionalInfo = any(),
                flow = any(),
                listener = capture(listenerSlot)
            )
        } answers {
            listenerSlot.captured.onCompleted(expectedStatus)
        }

        val observer = mockk<Observer<PaymentMethodsViewModel.PaymentFlowStatus>>(relaxed = true)
        viewModel.paymentFlowStatus.observeForever(observer)

        viewModel.startCheckout(availablePaymentMethodType)

        advanceUntilIdle()

        assertEquals(
            (viewModel.paymentFlowStatus.value as? PaymentMethodsViewModel.PaymentFlowStatus.SchemaFieldsDialog)?.typeInfo,
            paymentMethodTypeInfo
        )

        viewModel.paymentFlowStatus.removeObserver(observer)
        unmockkObject(AirwallexLogger)
    }

    @Test
    fun `test checkoutWithSchemaFields when banks is empty`() =
        runTest {
            val availablePaymentMethodType = createAvailablePaymentMethodType()
            val paymentMethodTypeInfo = createPaymentMethodTypeInfo(SAMPLE_BANK_FIELD)
            val bankResponse = createBankResponse()
            val viewModel = mockViewModel(TransactionMode.ONE_OFF)
            val expectedStatus = AirwallexPaymentStatus.Success("test_payment_intent_id")

            val mockListener = slot<Airwallex.PaymentListener<PaymentMethodTypeInfo>>()
            val mockBankListener = slot<Airwallex.PaymentListener<BankResponse>>()

            every {
                airwallex.retrievePaymentMethodTypeInfo(any(), capture(mockListener))
            } answers {
                mockListener.captured.onSuccess(paymentMethodTypeInfo)
            }

            every {
                airwallex.retrieveBanks(any(), capture(mockBankListener))
            } answers {
                mockBankListener.captured.onSuccess(bankResponse)
            }

            val listenerSlot = slot<Airwallex.PaymentResultListener>()
            coEvery {
                airwallex.checkout(
                    session = any(),
                    paymentMethod = any(),
                    additionalInfo = any(),
                    flow = any(),
                    listener = capture(listenerSlot)
                )
            } answers {
                listenerSlot.captured.onCompleted(expectedStatus)
            }

            viewModel.startCheckout(availablePaymentMethodType)

            advanceUntilIdle()

            assertEquals(
                (viewModel.paymentFlowStatus.value as? PaymentMethodsViewModel.PaymentFlowStatus.BankDialog)?.banks,
                null
            )
            unmockkObject(AirwallexLogger)
        }

    @Test
    fun `test checkoutWithSchemaFields when banks is not empty`() =
        runTest {
            val availablePaymentMethodType = createAvailablePaymentMethodType()
            val paymentMethodTypeInfo = createPaymentMethodTypeInfo(SAMPLE_BANK_FIELD)
            val bankResponse = createBankResponse(SAMPLE_BANK_RESPONSE)
            val viewModel = mockViewModel(TransactionMode.ONE_OFF)
            val expectedStatus = AirwallexPaymentStatus.Success("test_payment_intent_id")

            val mockListener = slot<Airwallex.PaymentListener<PaymentMethodTypeInfo>>()
            val mockBankListener = slot<Airwallex.PaymentListener<BankResponse>>()

            every {
                airwallex.retrievePaymentMethodTypeInfo(any(), capture(mockListener))
            } answers {
                mockListener.captured.onSuccess(paymentMethodTypeInfo)
            }

            every {
                airwallex.retrieveBanks(any(), capture(mockBankListener))
            } answers {
                mockBankListener.captured.onSuccess(bankResponse)
            }

            val listenerSlot = slot<Airwallex.PaymentResultListener>()
            coEvery {
                airwallex.checkout(
                    session = any(),
                    paymentMethod = any(),
                    additionalInfo = any(),
                    flow = any(),
                    listener = capture(listenerSlot)
                )
            } answers {
                listenerSlot.captured.onCompleted(expectedStatus)
            }

            viewModel.startCheckout(availablePaymentMethodType)

            advanceUntilIdle()

            assertEquals(
                (viewModel.paymentFlowStatus.value as? PaymentMethodsViewModel.PaymentFlowStatus.BankDialog)?.banks,
                bankResponse.items
            )
            unmockkObject(AirwallexLogger)
        }

    @Test
    fun `test startCheckout invokes checkout directly when requireHandleSchemaFields is false`() =
        runTest {
            val availablePaymentMethodType = createAvailablePaymentMethodType()
            val viewModel = mockViewModel(TransactionMode.RECURRING)
            val listenerSlot = slot<Airwallex.PaymentResultListener>()
            val expectedStatus = AirwallexPaymentStatus.Success("test_payment_intent_id")

            mockkObject(AirwallexLogger)
            coEvery {
                airwallex.checkout(
                    session = any(),
                    paymentMethod = any(),
                    additionalInfo = any(),
                    flow = any(),
                    listener = capture(listenerSlot)
                )
            } answers {
                listenerSlot.captured.onCompleted(expectedStatus)
            }

            viewModel.startCheckout(availablePaymentMethodType)
            advanceUntilIdle()
            verify {
                AirwallexLogger.info(eq("PaymentMethodsViewModel startCheckout, type = card"), isNull())
            }
            coVerify { viewModel["trackPaymentSuccess"](expectedStatus, "card") }
            val flowStatus =
                viewModel.paymentFlowStatus.value as PaymentMethodsViewModel.PaymentFlowStatus.PaymentStatus
            assertEquals(
                "test_payment_intent_id",
                (flowStatus.status as AirwallexPaymentStatus.Success).paymentIntentId
            )
            unmockkObject(AirwallexLogger)
        }

    @Test
    fun `test fetchPaymentMethodsAndConsents fails when hasSinglePaymentMethod is true`() = runTest {
        val availablePaymentConsents =
            createPaymentConsents("""{"items":[], "has_more":false}""".trimIndent())
        val availableMethodTypes = createPaymentMethods(TransactionMode.ONE_OFF)
        val viewModel = mockViewModel(
            transactionMode = TransactionMode.ONE_OFF,
            paymentMethods = listOf("card"),
            paymentConsents = availablePaymentConsents,
            availablePaymentMethods = availableMethodTypes
        )
        mockkObject(AirwallexLogger)
        every { AirwallexLogger.info(any<String>()) } just Runs

        viewModel.fetchPaymentMethodsAndConsents()

        advanceUntilIdle()
        verify {
            AirwallexLogger.info(any())
        }

        assertEquals(
            (viewModel.paymentMethodResult.value as? PaymentMethodsViewModel.PaymentMethodResult.Skip)?.schemes,
            availableMethodTypes.items[0].cardSchemes ?: emptyList()
        )

        unmockkObject(AirwallexLogger)
    }

    @Test
    fun `test fetchPaymentMethodsAndConsents fails when hasSinglePaymentMethod is false`() = runTest {
        val availablePaymentConsents = createPaymentConsents()
        val availableMethodTypes = createPaymentMethods(TransactionMode.ONE_OFF)
        val viewModel = mockViewModel(
            transactionMode = TransactionMode.ONE_OFF,
            paymentMethods = listOf("card"),
            paymentConsents = availablePaymentConsents,
            availablePaymentMethods = availableMethodTypes
        )
        mockkObject(AirwallexLogger)
        every { AirwallexLogger.info(any<String>()) } just Runs

        viewModel.fetchPaymentMethodsAndConsents()

        advanceUntilIdle()
        verify {
            AirwallexLogger.info(any())
        }

        assertEquals(
            (viewModel.paymentMethodResult.value as? PaymentMethodsViewModel.PaymentMethodResult.Show)?.methods?.first?.get(
                0
            ),
            availableMethodTypes.items[0]
        )
        unmockkObject(AirwallexLogger)
    }

    @Test
    fun `test fetchPaymentMethodsAndConsents fails when result is failure`() = runTest {
        val viewModel = mockViewModel(TransactionMode.RECURRING, secretClient = "")
        mockkObject(AirwallexLogger)

        viewModel.fetchPaymentMethodsAndConsents()
        advanceUntilIdle()

        assertEquals(
            "Client secret is empty or blank",
            (viewModel.paymentFlowStatus.value as? PaymentMethodsViewModel.PaymentFlowStatus.ErrorAlert)?.message
        )
        unmockkObject(AirwallexLogger)
    }

    @Test
    fun `test fetchAvailablePaymentMethodTypes handles AirwallexException`() = runBlocking {
        val viewModel = mockViewModel(TransactionMode.ONE_OFF)
        val spyViewModel = spyk(viewModel)
        coEvery {
            spyViewModel["filterPaymentMethodsBySession"](
                any<List<AvailablePaymentMethodType>>(),
                any<List<String>>()
            )
        } throws
                AirwallexCheckoutException(message = "exception")
        val result = try {
            spyViewModel.fetchAvailablePaymentMethodsAndConsents()
        } catch (e: Exception) {
            Result.failure(e)
        }
        verify(exactly = 1) { AirwallexLogger.error(any(), any()) }
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AirwallexCheckoutException)
    }

    @Test
    fun `fetchAvailablePaymentMethodTypes returns failure when clientSecret is empty`() =
        runBlocking {
            val viewModel = mockViewModel(TransactionMode.ONE_OFF, secretClient = "")
            val result = viewModel.fetchAvailablePaymentMethodsAndConsents()
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is AirwallexCheckoutException)
            assertTrue(result.exceptionOrNull()?.message == "Client secret is empty or blank")
        }

    @Test
    fun `test fetchAvailablePaymentMethodTypes when session is recurring and has client secret`() =
        runTest {
            val viewModel = mockViewModel(transactionMode = TransactionMode.RECURRING)
            val result = viewModel.fetchAvailablePaymentMethodsAndConsents().getOrNull()
            assertEquals(result?.first?.first()?.name, "card")
            verify(exactly = 1) { TokenManager.updateClientSecret(any()) }
        }

    @Test
    fun `test fetchAvailablePaymentMethodTypes when hidePaymentConsents is true`() =
        runTest {
            val viewModel =
                mockViewModel(transactionMode = TransactionMode.ONE_OFF, hidePaymentConsents = true)
            viewModel.fetchAvailablePaymentMethodsAndConsents()
            coVerify(exactly = 0) { airwallex.retrieveAvailablePaymentConsents(any()) }
        }

    @Test
    fun `test fetchAvailablePaymentMethodTypes when hidePaymentConsents is false`() =
        runTest {
            val viewModel = mockViewModel(
                transactionMode = TransactionMode.ONE_OFF,
                hidePaymentConsents = false
            )
            viewModel.fetchAvailablePaymentMethodsAndConsents()
            coVerify { airwallex.retrieveAvailablePaymentConsents(any()) }
        }

    @Test
    fun `test fetchAvailablePaymentMethodTypes when paymentMethods is null`() =
        runTest {
            val viewModel =
                mockViewModel(transactionMode = TransactionMode.ONE_OFF)
            viewModel.fetchAvailablePaymentMethodsAndConsents()
            val result = viewModel.fetchAvailablePaymentMethodsAndConsents().getOrNull()
            assertEquals(result?.first?.first()?.name, "card")
        }

    @Test
    fun `test fetchAvailablePaymentMethodTypes when paymentMethods is not null`() =
        runTest {
            val viewModel =
                mockViewModel(
                    transactionMode = TransactionMode.ONE_OFF,
                    paymentMethods = listOf("card")
                )
            viewModel.fetchAvailablePaymentMethodsAndConsents()
            val result = viewModel.fetchAvailablePaymentMethodsAndConsents()?.getOrNull()
            assertEquals(result?.first?.first()?.name, "card")
        }

    @Test
    fun `test fetchAvailablePaymentMethodTypes when session is oneoff`() = runTest {
        val viewModel = mockViewModel(transactionMode = TransactionMode.ONE_OFF)
        val result = viewModel.fetchAvailablePaymentMethodsAndConsents().getOrNull()
        assertEquals(result?.first?.first()?.name, "card")
        verify(exactly = 1) { TokenManager.updateClientSecret(any()) }
    }

    @Test
    fun `test analytics logging`() {
        val viewModel = mockViewModel(transactionMode = TransactionMode.ONE_OFF)
        assertEquals(viewModel.pageName, "payment_method_list")

        val paymentConsent = PaymentConsent(
            paymentMethod = PaymentMethod.Builder().setType(PaymentMethodType.REDIRECT.value)
                .build()
        )
        viewModel.trackPaymentSelection(paymentConsent.paymentMethod?.type)
        verify(exactly = 1) {
            AnalyticsLogger.logAction(
                "select_payment",
                mapOf("payment_method" to "redirect")
            )
        }
        viewModel.trackCardPaymentSelection()
        verify(exactly = 1) {
            AnalyticsLogger.logAction(
                "select_payment",
                mapOf("payment_method" to "card")
            )
        }
        viewModel.trackPaymentSuccess(paymentConsent.paymentMethod?.type)
        verify(exactly = 1) {
            AnalyticsLogger.logAction(
                "payment_success",
                mapOf("payment_method" to "redirect")
            )
        }
        viewModel.trackCardPaymentSuccess()
        verify(exactly = 1) {
            AnalyticsLogger.logAction(
                "payment_success",
                mapOf("payment_method" to "card")
            )
        }
    }

    @Suppress("LongMethod", "LongParameterList")
    private fun mockViewModel(
        transactionMode: TransactionMode,
        hidePaymentConsents: Boolean = false,
        paymentMethods: List<String> = emptyList(),
        secretClient: String = "qadf",
        paymentConsents: Page<PaymentConsent>? = null,
        availablePaymentMethods: Page<AvailablePaymentMethodType>? = null,
    ):
            PaymentMethodsViewModel {
        val application = mockk<Application>()
        airwallex = mockk<Airwallex>()
        mockkObject(TokenManager)
        coEvery { TokenManager.updateClientSecret(any()) } returns Unit
        val session = when (transactionMode) {
            TransactionMode.RECURRING -> AirwallexRecurringSession.Builder(
                nextTriggerBy = PaymentConsent.NextTriggeredBy.CUSTOMER,
                customerId = "cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu",
                currency = "AUD",
                amount = BigDecimal.valueOf(100.01),
                countryCode = "CN",
                clientSecret = secretClient
            )
                .setMerchantTriggerReason(PaymentConsent.MerchantTriggerReason.SCHEDULED)
                .setRequireCvc(true)
                .setPaymentMethods(paymentMethods)
                .build()

            TransactionMode.ONE_OFF -> AirwallexPaymentSession.Builder(
                PaymentIntent(
                    id = "id",
                    amount = BigDecimal.valueOf(100.01),
                    currency = "AUD",
                    clientSecret = secretClient,
                    customerId = "cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu",
                ),
                "AU",
                GooglePayOptions()
            )
                .setHidePaymentConsents(hidePaymentConsents)
                .setPaymentMethods(paymentMethods)
                .build()
        }
        coEvery {
            airwallex.retrieveAvailablePaymentMethods(
                any(),
                any()
            )
        } returns (availablePaymentMethods ?: createPaymentMethods(transactionMode))
        coEvery { airwallex.retrieveAvailablePaymentConsents(any()) } returns (
                paymentConsents
                    ?: createPaymentConsents()
                )
        return PaymentMethodsViewModel(application, airwallex, session)
    }
}
