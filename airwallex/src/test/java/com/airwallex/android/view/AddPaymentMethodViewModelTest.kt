package com.airwallex.android.view

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.airwallex.android.R
import com.airwallex.android.core.*
import com.airwallex.android.core.model.*
import io.mockk.*
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AddPaymentMethodViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val application = mockk<Application>()
    private val airwallex: Airwallex = mockk()
    private val visaCardNumber = "4242424242424242"

    @Test
    fun `one off payment method includes provided billing when information is required by session`() {
        val card: PaymentMethod.Card = mockk()
        val billing: Billing = mockk()
        val session: AirwallexPaymentSession = mockk()

        every { session.isBillingInformationRequired } returns true
        every { card.cvc } returns "123"

        val viewModel = createViewModel(session)
        val payment = viewModel.createPaymentMethod(card, billing)
        val result =
            requireNotNull(payment.value as? AddPaymentMethodViewModel.PaymentMethodResult.Success)
        val resultBilling = requireNotNull(result.paymentMethod.billing)

        assertEquals(resultBilling, billing)
    }

    @Test
    fun `one off payment method does not include provided billing when information is not required by session`() {
        val card: PaymentMethod.Card = mockk()
        val billing: Billing = mockk()
        val session: AirwallexPaymentSession = mockk()

        every { session.isBillingInformationRequired } returns false
        every { card.cvc } returns "123"

        val viewModel = createViewModel(session)
        val payment = viewModel.createPaymentMethod(card, billing)
        val result =
            requireNotNull(payment.value as? AddPaymentMethodViewModel.PaymentMethodResult.Success)
        val resultBilling = result.paymentMethod.billing

        assertNull(resultBilling)
        assertNotEquals(resultBilling, billing)
    }

    @Test
    fun `one off payment card missing cvc error`() {
        val card: PaymentMethod.Card = mockk()
        val billing: Billing = mockk()
        val session: AirwallexPaymentSession = mockk()

        every { session.isBillingInformationRequired } returns true
        every { card.cvc } returns null

        val viewModel = createViewModel(session)
        val payment = viewModel.createPaymentMethod(card, billing)
        val result =
            requireNotNull(payment.value as? AddPaymentMethodViewModel.PaymentMethodResult.Error)

        val errorMessage = requireNotNull(result.exception.message)
        assertTrue { errorMessage.contains("CVC missing") }
    }

    @Test
    fun `stored payment method includes provided billing when information is required by session`() {
        val clientSecret = mockk<ClientSecret>(relaxed = true)
        val paymentMethod = mockk<PaymentMethod>()
        val clientSecretRepository = mockk<ClientSecretRepository>()
        val clientSecretListener = slot<ClientSecretRepository.ClientSecretRetrieveListener>()
        val paymentListener = slot<Airwallex.PaymentListener<PaymentMethod>>()

        mockkObject(ClientSecretRepository)
        every { ClientSecretRepository.getInstance() } returns clientSecretRepository
        every {
            clientSecretRepository.retrieveClientSecret(
                any(),
                capture(clientSecretListener)
            )
        } answers {
            clientSecretListener.captured.onClientSecretRetrieve(clientSecret)
        }
        every { airwallex.createPaymentMethod(any(), capture(paymentListener)) } answers {
            paymentListener.captured.onSuccess(paymentMethod)
        }

        val customerID = "Test_ID"
        val card: PaymentMethod.Card = mockk()
        val billing: Billing = mockk()
        val session: AirwallexRecurringSession = mockk()

        every { session.customerId } returns customerID
        every { session.isBillingInformationRequired } returns true
        every { card.cvc } returns "123"

        val viewModel = createViewModel(session)
        val payment = viewModel.createPaymentMethod(card, billing)
        val result =
            requireNotNull(payment.value as? AddPaymentMethodViewModel.PaymentMethodResult.Success)

        assertEquals(result.paymentMethod, paymentMethod)

        val params = CreatePaymentMethodParams(
            clientSecret.value,
            customerID,
            card,
            billing
        )
        verify { airwallex.createPaymentMethod(params, any()) }
    }

    @Test
    fun `stored payment method does not include provided billing when information is not required by session`() {
        val clientSecret = mockk<ClientSecret>(relaxed = true)
        val paymentMethod = mockk<PaymentMethod>()
        val clientSecretRepository = mockk<ClientSecretRepository>()
        val clientSecretListener = slot<ClientSecretRepository.ClientSecretRetrieveListener>()
        val paymentListener = slot<Airwallex.PaymentListener<PaymentMethod>>()

        mockkObject(ClientSecretRepository)
        every { ClientSecretRepository.getInstance() } returns clientSecretRepository
        every {
            clientSecretRepository.retrieveClientSecret(
                any(),
                capture(clientSecretListener)
            )
        } answers {
            clientSecretListener.captured.onClientSecretRetrieve(clientSecret)
        }
        every { airwallex.createPaymentMethod(any(), capture(paymentListener)) } answers {
            paymentListener.captured.onSuccess(paymentMethod)
        }

        val customerID = "Test_ID"
        val card: PaymentMethod.Card = mockk()
        val billing: Billing = mockk()
        val session: AirwallexRecurringWithIntentSession = mockk()

        every { session.customerId } returns customerID
        every { session.isBillingInformationRequired } returns false
        every { card.cvc } returns "123"

        val viewModel = createViewModel(session)
        val payment = viewModel.createPaymentMethod(card, billing)
        val result =
            requireNotNull(payment.value as? AddPaymentMethodViewModel.PaymentMethodResult.Success)

        assertEquals(result.paymentMethod, paymentMethod)

        val params = CreatePaymentMethodParams(
            clientSecret.value,
            customerID,
            card,
            null
        )
        verify { airwallex.createPaymentMethod(params, any()) }
    }

    @Test
    fun `test validation error when card number is empty`() {
        val viewModel = createViewModel(mockk())
        assertEquals(
            viewModel.getValidationResult(""),
            AddPaymentMethodViewModel.ValidationResult.Error(R.string.airwallex_empty_card_number)
        )
    }

    @Test
    fun `test validation success when card number is visa and in supported schemes`() {
        val viewModel = createViewModel(mockk(), listOf(CardScheme("visa")))
        assertEquals(
            viewModel.getValidationResult(visaCardNumber),
            AddPaymentMethodViewModel.ValidationResult.Success
        )
    }

    @Test
    fun `test validation error when card number is visa but not in supported schemes`() {
        val viewModel = createViewModel(mockk(), listOf(CardScheme("mastercard")))
        assertEquals(
            viewModel.getValidationResult(visaCardNumber),
            AddPaymentMethodViewModel.ValidationResult.Error(R.string.airwallex_unsupported_card_number)
        )
    }

    @Test
    fun `test validation error when card number is not valid`() {
        val viewModel = createViewModel(mockk())
        assertEquals(
            viewModel.getValidationResult("1234556"),
            AddPaymentMethodViewModel.ValidationResult.Error(R.string.airwallex_invalid_card_number)
        )
    }

    private fun createViewModel(
        session: AirwallexSession,
        cardSchemes: List<CardScheme> = listOf()
    ): AddPaymentMethodViewModel =
        AddPaymentMethodViewModel.Factory(application, airwallex, session, cardSchemes)
            .create(AddPaymentMethodViewModel::class.java)
}