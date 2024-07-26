package com.airwallex.android.view

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.airwallex.android.core.*
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.*
import com.airwallex.android.core.model.parser.AvailablePaymentMethodTypeParser
import com.airwallex.android.core.model.parser.PageParser
import com.airwallex.android.core.model.parser.PaymentConsentParser
import io.mockk.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class PaymentMethodsViewModelTest {
    private lateinit var airwallex: Airwallex

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        mockkObject(AnalyticsLogger)
        mockkObject(AirwallexLogger)
    }

    @After
    fun unmock() {
        unmockkAll()
    }

    @Test
    fun `test filterRequiredFields returns correct fields`() {
        val dynamicSchemaField1 = mockk<DynamicSchemaField> {
            every { hidden } returns false
        }
        val dynamicSchemaField2 = mockk<DynamicSchemaField> {
            every { hidden } returns true
        }

        val schema = mockk<DynamicSchema> {
            every { transactionMode } returns TransactionMode.ONE_OFF
            every { fields } returns listOf(dynamicSchemaField1, dynamicSchemaField2)
        }
        val info = mockk<PaymentMethodTypeInfo> {
            every { fieldSchemas } returns listOf(schema)
        }
        val viewModel = mockViewModel(TransactionMode.ONE_OFF)
        val result = viewModel.filterRequiredFields(info)
        assertEquals(1, result?.count())
        assertTrue(result?.any { !it.hidden } ?: false)
    }

    @Test
    fun `test fetchPaymentFlow returns IN_APP flow when candidate is present`() {
        val candidate = mockk<DynamicSchemaFieldCandidate>()
        every { candidate.value } returns AirwallexPaymentRequestFlow.IN_APP.value
        val flowField = mockk<DynamicSchemaField> {
            every { candidates } returns listOf(candidate)
            every { name } returns "flow" // Assuming fetchPaymentFlow uses this name internally
        }
        val schema = mockk<DynamicSchema> {
            every { transactionMode } returns TransactionMode.ONE_OFF
            every { fields } returns listOf(flowField)
        }
        val info = mockk<PaymentMethodTypeInfo> {
            every { fieldSchemas } returns listOf(schema)
        }
        val viewModel = mockViewModel(TransactionMode.ONE_OFF)
        val result = viewModel.fetchPaymentFlow(info)
        assertEquals(AirwallexPaymentRequestFlow.IN_APP, result)
    }

    @Test
    fun `test fetchPaymentFlow returns IN_APP flow when no candidates or defaults`() {
        val schema = mockk<DynamicSchema> {
            every { transactionMode } returns TransactionMode.ONE_OFF
            every { fields } returns emptyList<DynamicSchemaField>()
        }
        val info = mockk<PaymentMethodTypeInfo> {
            every { fieldSchemas } returns listOf(schema)
        }
        val viewModel = mockViewModel(TransactionMode.ONE_OFF)
        val result = viewModel.fetchPaymentFlow(info)
        assertEquals(AirwallexPaymentRequestFlow.IN_APP, result)
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
    fun `test fetchAvailablePaymentMethodsAndConsents handles AirwallexException`() = runBlocking {
        val viewModel = mockViewModel(TransactionMode.ONE_OFF)
        val spyViewModel = spyk(viewModel)
        coEvery { spyViewModel["filterPaymentMethodsBySession"](any<List<AvailablePaymentMethodType>>(), any<List<String>>()) } throws
                AirwallexCheckoutException(message = "exception")
        val result = try {
            spyViewModel.fetchAvailablePaymentMethodsAndConsents()
        } catch (e: Exception) {
            Result.failure(e)
        }
        verify(exactly = 1) { AirwallexLogger.error(any(), any()) }
        assertTrue(result != null && result.isFailure)
        assertTrue(result?.exceptionOrNull() is AirwallexCheckoutException)
    }

    @Test
    fun `fetchAvailablePaymentMethodsAndConsents returns failure when clientSecret is empty`() = runBlocking {
        val viewModel = mockViewModel(TransactionMode.ONE_OFF, secretClient = "")
        val result = viewModel.fetchAvailablePaymentMethodsAndConsents()
        assertTrue(result != null && result.isFailure)
        assertTrue(result?.exceptionOrNull() is AirwallexCheckoutException)
        assertTrue(result?.exceptionOrNull()?.message == "Client secret is empty or blank")
    }

    @Test
    fun `test fetchAvailablePaymentMethodTypes when session is recurring and has client secret`() =
        runTest {
            val viewModel = mockViewModel(transactionMode = TransactionMode.RECURRING)
            val result = viewModel.fetchAvailablePaymentMethodsAndConsents()?.getOrNull()
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
            val result = viewModel.fetchAvailablePaymentMethodsAndConsents()?.getOrNull()
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
    fun `test has single payment method - no consents, only one payment method`() = runTest {
        val viewModel = mockViewModel(TransactionMode.RECURRING)
        val cardPaymentMethod = mockk<AvailablePaymentMethodType>()
        every { cardPaymentMethod.name } returns PaymentMethodType.CARD.name
        val hasSinglePaymentMethod = viewModel.hasSinglePaymentMethod(
            desiredPaymentMethodType = cardPaymentMethod,
            paymentMethods = listOf(cardPaymentMethod),
            consents = emptyList()
        )
        assertTrue(hasSinglePaymentMethod)
    }

    @Test
    fun `test does not have single payment method - has consents, only one payment method`() =
        runTest {
            val viewModel = mockViewModel(TransactionMode.RECURRING)
            val cardPaymentMethod = mockk<AvailablePaymentMethodType>()
            every { cardPaymentMethod.name } returns PaymentMethodType.CARD.name

            val hasSinglePaymentMethod = viewModel.hasSinglePaymentMethod(
                desiredPaymentMethodType = cardPaymentMethod,
                paymentMethods = listOf(cardPaymentMethod),
                consents = listOf(PaymentConsent())
            )
            assertFalse(hasSinglePaymentMethod)
        }

    @Test
    fun `test does not have single payment method - no consents, multiple payment method`() =
        runTest {
            val viewModel = mockViewModel(TransactionMode.RECURRING)

            val cardPaymentMethod = mockk<AvailablePaymentMethodType>()
            every { cardPaymentMethod.name } returns PaymentMethodType.CARD.name

            val redirectPaymentMethod = mockk<AvailablePaymentMethodType>()
            every { redirectPaymentMethod.name } returns PaymentMethodType.REDIRECT.name

            val hasSinglePaymentMethod = viewModel.hasSinglePaymentMethod(
                desiredPaymentMethodType = cardPaymentMethod,
                paymentMethods = listOf(cardPaymentMethod, redirectPaymentMethod),
                consents = emptyList()
            )
            assertFalse(hasSinglePaymentMethod)
        }

    @Test
    fun `test does not have single payment method - has consents, multiple payment method`() =
        runTest {
            val viewModel = mockViewModel(TransactionMode.RECURRING)

            val cardPaymentMethod = mockk<AvailablePaymentMethodType>()
            every { cardPaymentMethod.name } returns PaymentMethodType.CARD.name

            val redirectPaymentMethod = mockk<AvailablePaymentMethodType>()
            every { redirectPaymentMethod.name } returns PaymentMethodType.REDIRECT.name

            val hasSinglePaymentMethod = viewModel.hasSinglePaymentMethod(
                desiredPaymentMethodType = cardPaymentMethod,
                paymentMethods = listOf(cardPaymentMethod, redirectPaymentMethod),
                consents = listOf(PaymentConsent())
            )
            assertFalse(hasSinglePaymentMethod)
        }

    @Test
    fun `test fetchAvailablePaymentMethodTypes when session is oneoff`() = runTest {
        val viewModel = mockViewModel(transactionMode = TransactionMode.ONE_OFF)
        val result = viewModel.fetchAvailablePaymentMethodsAndConsents()?.getOrNull()
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
        viewModel.trackPaymentSelection(paymentConsent)
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
        viewModel.trackPaymentSuccess(paymentConsent)
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

    @Suppress("LongMethod")
    private fun mockViewModel(
        transactionMode: TransactionMode,
        hidePaymentConsents: Boolean = false,
        paymentMethods: List<String> = emptyList(),
        secretClient: String = "qadf"
    ):
            PaymentMethodsViewModel {

        val mockConsents = PageParser(PaymentConsentParser()).parse(
            JSONObject(
                """
        {
            "items":[
                {
                  "payment_method": {
                    "type": "card",
                    "card": {
                        "name": "John",
                        "issuer_name": "DISCOVER BANK",
                        "is_commercial": false,
                        "number_type": "PAN"
                    }
                  },
                  "next_triggered_by": "customer",
                  "status": "VERIFIED"
                }   
            ],
            "has_more":false
        }
                """.trimIndent()
            )
        )
        val mockMethods = PageParser(AvailablePaymentMethodTypeParser()).parse(
            JSONObject(
                """
        {
            "items":[ 
                  {
                   "name":"card",
                   "transaction_mode":${transactionMode.value},
                   "active":true,
                   "transaction_currencies":["dollar","RMB"],
                   "flows":["inapp"]
                  }   
            ],
            "has_more":false
        }
                """.trimIndent()
            )
        )
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
        coEvery { airwallex.retrieveAvailablePaymentMethods(any(), any()) } returns mockMethods
        coEvery { airwallex.retrieveAvailablePaymentConsents(any()) } returns mockConsents
        return PaymentMethodsViewModel(application, airwallex, session)
    }
}
