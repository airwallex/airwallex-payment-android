package com.airwallex.android.view

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.airwallex.android.R
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.AirwallexRecurringSession
import com.airwallex.android.core.AirwallexRecurringWithIntentSession
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.CardBrand
import com.airwallex.android.core.model.Address
import com.airwallex.android.core.model.Billing
import com.airwallex.android.core.model.CardScheme
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.Shipping
import com.airwallex.android.view.util.ExpiryDateUtils
import com.airwallex.android.view.util.createExpiryMonthAndYear
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AddPaymentMethodViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val application = mockk<Application>()
    private val airwallex: Airwallex = mockk(relaxed = true)
    private val visaCardNumber = "4242424242424242"

    @Before
    fun setup() {
        mockkObject(ExpiryDateUtils)
        mockkStatic("com.airwallex.android.view.util.ExpiryDateUtilsKt")
        every { application.getString(any()) } returns "Test String"
        every { application.getString(R.string.airwallex_confirm) } returns "Confirm"
        every { application.getString(R.string.airwallex_pay_now) } returns "Pay Now"
    }

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
        val viewModel =
            createViewModel(mockk(), listOf(CardScheme("mastercard"), CardScheme("visa")))
        assertEquals(viewModel.pageName, "card_payment_view")
        assertEquals(
            viewModel.additionalInfo, mapOf("supportedSchemes" to listOf("mastercard", "visa"))
        )
    }

    @Test
    fun `test cardHolderName returns empty when no shipping`() {
        val viewModel = createViewModel(mockk())
        assertEquals("", viewModel.cardHolderName)
    }

    @Test
    fun `test cardHolderName combines first and last name from shipping`() {
        val mockShipping = mockk<Shipping> {
            every { firstName } returns "John"
            every { lastName } returns "Doe"
        }
        val mockSession = mockk<AirwallexRecurringSession> {
            every { shipping } returns mockShipping
        }
        val viewModel = createViewModel(mockSession)
        assertEquals("John Doe", viewModel.cardHolderName)
    }

    @Test
    fun `test countryCode returns from session`() {
        val expectedCountryCode = "GB"
        val mockSession = mockk<AirwallexSession> {
            every { countryCode } returns expectedCountryCode
            every { isBillingInformationRequired } returns false
            every { isEmailRequired } returns false
        }
        val viewModel = createViewModel(mockSession)
        assertEquals(expectedCountryCode, viewModel.countryCode)
    }

    @Test
    fun `test getCardNumberValidationMessage with empty input`() {
        val viewModel = createViewModel(mockk())
        assertEquals(
            R.string.airwallex_empty_card_number, viewModel.getCardNumberValidationMessage("")
        )
    }

    @Test
    fun `test getCardNumberValidationMessage with invalid card`() {
        val viewModel = createViewModel(mockk())
        assertEquals(
            R.string.airwallex_invalid_card_number, viewModel.getCardNumberValidationMessage("1234")
        )
    }

    @Test
    fun `test getCardNumberValidationMessage with unsupported card`() {
        val viewModel = createViewModel(mockk(), listOf(CardScheme("mastercard")))
        assertEquals(
            R.string.airwallex_unsupported_card_number,
            viewModel.getCardNumberValidationMessage(visaCardNumber)
        )
    }

    @Test
    fun `test getCardNumberValidationMessage with valid card`() {
        val viewModel = createViewModel(mockk(), listOf(CardScheme("visa")))
        assertNull(viewModel.getCardNumberValidationMessage(visaCardNumber))
    }

    @Test
    fun `test getExpiryValidationMessage with empty input`() {
        val viewModel = createViewModel(mockk())
        every { ExpiryDateUtils.isValidExpiryDate(any()) } returns false
        assertEquals(R.string.airwallex_empty_expiry, viewModel.getExpiryValidationMessage(""))
    }

    @Test
    fun `test getExpiryValidationMessage with invalid expiry`() {
        val viewModel = createViewModel(mockk())
        every { ExpiryDateUtils.isValidExpiryDate(any()) } returns false
        assertEquals(
            R.string.airwallex_invalid_expiry_date, viewModel.getExpiryValidationMessage("13/25")
        )
    }

    @Test
    fun `test getExpiryValidationMessage with valid expiry`() {
        val viewModel = createViewModel(mockk())
        every { ExpiryDateUtils.isValidExpiryDate(any()) } returns true
        assertNull(viewModel.getExpiryValidationMessage("12/25"))
    }

    @Test
    fun `test getCvvValidationMessage with empty input`() {
        val viewModel = createViewModel(mockk())
        assertEquals(
            R.string.airwallex_empty_cvc, viewModel.getCvvValidationMessage("", CardBrand.Visa)
        )
    }

    @Test
    fun `test getCvvValidationMessage with invalid CVV length`() {
        val viewModel = createViewModel(mockk())
        assertEquals(
            R.string.airwallex_invalid_cvc, viewModel.getCvvValidationMessage("12", CardBrand.Visa)
        )
    }

    @Test
    fun `test getCvvValidationMessage with valid CVV`() {
        val viewModel = createViewModel(mockk())
        assertNull(viewModel.getCvvValidationMessage("123", CardBrand.Visa))
        assertNull(viewModel.getCvvValidationMessage("1234", CardBrand.Amex))
    }

    @Test
    fun `test getCardHolderNameValidationMessage with empty input`() {
        val viewModel = createViewModel(mockk())
        assertEquals(
            R.string.airwallex_empty_card_name, viewModel.getCardHolderNameValidationMessage("")
        )
    }

    @Test
    fun `test getCardHolderNameValidationMessage with valid name`() {
        val viewModel = createViewModel(mockk())
        assertNull(viewModel.getCardHolderNameValidationMessage("John Doe"))
    }

    @Test
    fun `test getEmailValidationMessage with empty input`() {
        val viewModel = createViewModel(mockk(), isEmailRequired = true)
        assertEquals(R.string.airwallex_empty_email, viewModel.getEmailValidationMessage(""))
    }

    @Test
    fun `test getEmailValidationMessage with invalid input`() {
        val viewModel = createViewModel(mockk(), isEmailRequired = true)
        assertEquals(R.string.airwallex_invalid_email, viewModel.getEmailValidationMessage("foo"))
    }

    @Test
    fun `test getEmailValidationMessage with valid input`() {
        val viewModel = createViewModel(mockk(), isEmailRequired = true)
        assertNull(viewModel.getEmailValidationMessage("foo@email.com"))
    }

    @Test
    fun `test getBillingValidationMessage with empty input`() {
        val viewModel = createViewModel(mockk())
        assertEquals(
            R.string.airwallex_empty_street, viewModel.getBillingValidationMessage(
                "", AddPaymentMethodViewModel.BillingFieldType.STREET
            )
        )
        assertEquals(
            R.string.airwallex_empty_city, viewModel.getBillingValidationMessage(
                "", AddPaymentMethodViewModel.BillingFieldType.CITY
            )
        )
        assertEquals(
            R.string.airwallex_empty_state, viewModel.getBillingValidationMessage(
                "", AddPaymentMethodViewModel.BillingFieldType.STATE
            )
        )
        assertEquals(
            R.string.airwallex_empty_postal_code, viewModel.getBillingValidationMessage(
                "", AddPaymentMethodViewModel.BillingFieldType.POSTAL_CODE
            )
        )
        assertEquals(
            R.string.airwallex_empty_phone_number, viewModel.getBillingValidationMessage(
                "", AddPaymentMethodViewModel.BillingFieldType.PONE_NUMBER
            )
        )
    }

    @Test
    fun `test getBillingValidationMessage with valid input`() {
        val viewModel = createViewModel(mockk())
        assertNull(
            viewModel.getBillingValidationMessage(
                "123 Main St", AddPaymentMethodViewModel.BillingFieldType.STREET
            )
        )
    }

    @Test
    fun `test createCard with valid input`() {
        val viewModel = createViewModel(mockk())
        val card = viewModel.createCard(visaCardNumber, "John Doe", "12/25", "123")
        assertNotNull(card)
        assertEquals(visaCardNumber, card.number)
        assertEquals("John Doe", card.name)
        assertEquals("12", card.expiryMonth)
        assertEquals("2025", card.expiryYear)
        assertEquals("123", card.cvc)
    }

    @Test
    fun `test createCard with invalid expiry`() {
        val viewModel = createViewModel(mockk())
        every { "13/25".createExpiryMonthAndYear() } returns null
        assertNull(viewModel.createCard(visaCardNumber, "John Doe", "13/25", "123"))
    }

    @Test
    fun `test createBillingWithShipping with valid input`() {
        val viewModel = createViewModel(mockk())
        val billing = viewModel.createBillingWithShipping(
            countryCode = "US",
            state = "CA",
            city = "San Francisco",
            street = "123 Main St",
            postcode = "94105",
            phoneNumber = "+14155551234",
            email = "test@example.com"
        )

        assertEquals("US", billing.address?.countryCode)
        assertEquals("CA", billing.address?.state)
        assertEquals("San Francisco", billing.address?.city)
        assertEquals("123 Main St", billing.address?.street)
        assertEquals("94105", billing.address?.postcode)
        assertEquals("+14155551234", billing.phone)
        assertEquals("test@example.com", billing.email)
    }

    private fun createViewModel(
        session: AirwallexSession,
        cardSchemes: List<CardScheme> = listOf(),
        isBillingRequired: Boolean = false,
        isEmailRequired: Boolean = false
    ): AddPaymentMethodViewModel {
        every { session.isBillingInformationRequired } returns isBillingRequired
        every { session.isEmailRequired } returns isEmailRequired
        // Don't set countryCode here, let the test set it up as needed
        every { application.getString(R.string.airwallex_confirm) } returns "Confirm"
        every { application.getString(R.string.airwallex_pay_now) } returns "Pay"
        return AddPaymentMethodViewModel.Factory(application, airwallex, session, cardSchemes)
            .create(AddPaymentMethodViewModel::class.java)
    }

    @Test
    fun `test confirmPayment calls airwallex with correct parameters`() {
        val session = mockk<AirwallexPaymentSession> {
            every { isBillingInformationRequired } returns true
            every { countryCode } returns "US"
        }
        val viewModel = createViewModel(session, isBillingRequired = true)

        val card = PaymentMethod.Card.Builder().setNumber("4111111111111111").setName("Test User")
            .setExpiryMonth("12").setExpiryYear("2025").setCvc("123").build()

        val billing = Billing.Builder().setAddress(
                Address.Builder().setCountryCode("US").setState("CA").setCity("San Francisco")
                    .setStreet("123 Main St").setPostcode("94105").build()
            ).setPhone("+1234567890").setEmail("test@example.com").build()

        // Mock the confirmPaymentIntent method
        every {
            airwallex.confirmPaymentIntent(
                session = session, card = card, billing = billing, saveCard = true, listener = any()
            )
        } returns Unit

        // When
        viewModel.confirmPayment(card, true, billing)

        // Then
        verify {
            airwallex.confirmPaymentIntent(
                session = session, card = card, billing = billing, saveCard = true, listener = any()
            )
        }
    }
}