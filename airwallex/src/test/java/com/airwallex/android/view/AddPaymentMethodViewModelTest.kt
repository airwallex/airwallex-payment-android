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
import kotlin.test.assertTrue

class AddPaymentMethodViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val application = mockk<Application>()
    private val airwallex: Airwallex = mockk()
    private val visaCardNumber = "4242424242424242"

    @Test
    fun `test shipping for AirwallexPaymentSession`() {
        val mockShipping = mockk<Shipping>(relaxed = true)
        val mockSession = mockk<AirwallexPaymentSession> {
            every { shipping } returns mockShipping
        }
        val viewModel = createViewModel(mockSession)
        assertEquals(mockShipping, viewModel.shipping)
    }

    @Test
    fun `test shipping for AirwallexRecurringWithIntentSession`() {
        val mockShipping = mockk<Shipping>(relaxed = true)
        val mockSession = mockk<AirwallexRecurringWithIntentSession> {
            every { shipping } returns mockShipping
        }
        val viewModel = createViewModel(mockSession)
        assertEquals(mockShipping, viewModel.shipping)
    }

    @Test
    fun `test shipping for AirwallexRecurringSession`() {
        val mockShipping = mockk<Shipping>(relaxed = true)
        val mockSession = mockk<AirwallexRecurringSession> {
            every { shipping } returns mockShipping
        }
        val viewModel = createViewModel(mockSession)
        assertEquals(mockShipping, viewModel.shipping)
    }

    @Test
    fun `test canSaveCard is true for AirwallexPaymentSession with customerId`() {
        val mockSession = mockk<AirwallexPaymentSession> {
            every { customerId } returns "someCustomerId"
        }
        val viewModel = createViewModel(mockSession)
        assertTrue(viewModel.canSaveCard)
    }

    @Test
    fun `test canSaveCard is false for AirwallexPaymentSession without customerId`() {
        val mockSession = mockk<AirwallexPaymentSession> {
            every { customerId } returns null
        }
        val viewModel = createViewModel(mockSession)
        assertTrue(!viewModel.canSaveCard)
    }

    @Test
    fun `test canSaveCard is false for other session types`() {
        val mockSession = mockk<AirwallexSession>()
        val viewModel = createViewModel(mockSession)
        assertTrue(!viewModel.canSaveCard)
    }

    @Test
    fun `one off payment method includes provided billing when information is required by session`() {
        val card: PaymentMethod.Card = mockk()
        val billing: Billing = mockk()
        val session: AirwallexPaymentSession = mockk()
        every { airwallex.confirmPaymentIntent(any(), card, billing, true, any()) } just Runs
        val viewModel = createViewModel(session)
        viewModel.confirmPayment(card, true, billing)
        verify { airwallex.confirmPaymentIntent(any(), card, billing, true, any()) }
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

    @Test
    fun `test page view tracking`() {
        val viewModel = createViewModel(mockk(), listOf(CardScheme("mastercard"), CardScheme("visa")))
        assertEquals(viewModel.pageName, "card_payment_view")
        assertEquals(viewModel.additionalInfo, mapOf("supportedSchemes" to listOf("mastercard", "visa")))
    }

    private fun createViewModel(
        session: AirwallexSession,
        cardSchemes: List<CardScheme> = listOf()
    ): AddPaymentMethodViewModel {
        every { application.getString(R.string.airwallex_confirm) } returns "Confirm"
        every { application.getString(R.string.airwallex_pay_now) } returns "Pay"
        return AddPaymentMethodViewModel.Factory(application, airwallex, session, cardSchemes)
            .create(AddPaymentMethodViewModel::class.java)
    }
}