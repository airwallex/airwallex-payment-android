package com.airwallex.android.view

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.airwallex.android.core.*
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.*
import com.airwallex.android.core.model.parser.AvailablePaymentMethodTypeParser
import com.airwallex.android.core.model.parser.PageParser
import com.airwallex.android.core.model.parser.PaymentConsentParser
import io.mockk.*
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
    }

    @After
    fun unmock() {
        unmockkObject(AnalyticsLogger)
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
    fun `test fetchAvailablePaymentMethodTypes when session is recurring and has no client secret`() =
        runTest {
            val viewModel = mockViewModel(false, TransactionMode.RECURRING)
            val result = viewModel.fetchAvailablePaymentMethodsAndConsents()
            assertEquals(result?.isFailure, true)
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
        val viewModel = mockViewModel(false, TransactionMode.RECURRING)
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
            val viewModel = mockViewModel(false, TransactionMode.RECURRING)
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
            val viewModel = mockViewModel(false, TransactionMode.RECURRING)

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
            val viewModel = mockViewModel(false, TransactionMode.RECURRING)

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
        verify(exactly = 1) { AnalyticsLogger.logAction("select_payment", mapOf("payment_method" to "redirect")) }
        viewModel.trackCardPaymentSelection()
        verify(exactly = 1) { AnalyticsLogger.logAction("select_payment", mapOf("payment_method" to "card")) }
        viewModel.trackPaymentSuccess(paymentConsent)
        verify(exactly = 1) { AnalyticsLogger.logAction("payment_success", mapOf("payment_method" to "redirect")) }
        viewModel.trackCardPaymentSuccess()
        verify(exactly = 1) { AnalyticsLogger.logAction("payment_success", mapOf("payment_method" to "card")) }
    }

    @Suppress("LongMethod")
    private fun mockViewModel(
        hasClientSecret: Boolean = true,
        transactionMode: TransactionMode,
        hidePaymentConsents: Boolean = false,
        paymentMethods: List<String> = emptyList()
    ):
            PaymentMethodsViewModel {
        mockkObject(TokenManager)
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

        val clientSecret = mockk<ClientSecret>(relaxed = true)
        val clientSecretRepository = mockk<ClientSecretRepository>()
        mockkObject(ClientSecretRepository)
        if (hasClientSecret) {
            coEvery { clientSecretRepository.retrieveClientSecret(any()) } returns clientSecret
        } else {
            coEvery { clientSecretRepository.retrieveClientSecret(any()) } throws AirwallexCheckoutException()
        }

        every { ClientSecretRepository.getInstance() } returns clientSecretRepository
        val application = mockk<Application>()
        airwallex = mockk<Airwallex>()
        val recurringSession = AirwallexRecurringSession.Builder(
            nextTriggerBy = PaymentConsent.NextTriggeredBy.CUSTOMER,
            customerId = "cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu",
            currency = "AUD",
            amount = BigDecimal.valueOf(100.01),
            countryCode = "CN"
        )
            .setMerchantTriggerReason(PaymentConsent.MerchantTriggerReason.SCHEDULED)
            .setRequireCvc(true)
            .setPaymentMethods(paymentMethods)
            .build()
        val oneOffSession = AirwallexPaymentSession.Builder(
            PaymentIntent(
                id = "id",
                amount = BigDecimal.valueOf(100.01),
                currency = "AUD",
                clientSecret = "qadf",
                customerId = "cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu",
            ),
            "AU",
            GooglePayOptions()
        )
            .setHidePaymentConsents(hidePaymentConsents)
            .setPaymentMethods(paymentMethods)
            .build()

        val session = when (transactionMode) {
            TransactionMode.ONE_OFF -> oneOffSession
            TransactionMode.RECURRING -> recurringSession
        }
        coEvery { airwallex.retrieveAvailablePaymentMethods(any(), any()) } returns mockMethods
        coEvery { airwallex.retrieveAvailablePaymentConsents(any()) } returns mockConsents
        return PaymentMethodsViewModel(application, airwallex, session)
    }
}
