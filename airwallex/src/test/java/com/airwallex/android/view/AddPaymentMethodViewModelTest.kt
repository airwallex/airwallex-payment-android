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
import kotlin.test.assertFalse
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
        val mockSession = mockk<AirwallexPaymentSession>(relaxed = true) {
            every { shipping } returns mockShipping
        }
        val viewModel = createViewModel(mockSession)
        assertEquals(mockShipping, viewModel.shipping)
    }

    @Test
    fun `test shipping for AirwallexRecurringWithIntentSession`() {
        val mockShipping = mockk<Shipping>(relaxed = true)
        val mockSession = mockk<AirwallexRecurringWithIntentSession>(relaxed = true) {
            every { shipping } returns mockShipping
        }
        val viewModel = createViewModel(mockSession)
        assertEquals(mockShipping, viewModel.shipping)
    }

    @Test
    fun `test shipping for AirwallexRecurringSession`() {
        val mockShipping = mockk<Shipping>(relaxed = true)
        val mockSession = mockk<AirwallexRecurringSession>(relaxed = true) {
            every { shipping } returns mockShipping
        }
        val viewModel = createViewModel(mockSession)
        assertEquals(mockShipping, viewModel.shipping)
    }

    @Test
    fun `test canSaveCard is true for AirwallexPaymentSession with customerId`() {
        val mockSession = mockk<AirwallexPaymentSession>(relaxed = true) {
            every { customerId } returns "someCustomerId"
        }
        val viewModel = createViewModel(mockSession)
        assertTrue(viewModel.canSaveCard)
    }

    @Test
    fun `test canSaveCard is false for AirwallexPaymentSession without customerId`() {
        val mockSession = mockk<AirwallexPaymentSession>(relaxed = true) {
            every { customerId } returns null
        }
        val viewModel = createViewModel(mockSession)
        assertTrue(!viewModel.canSaveCard)
    }

    @Test
    fun `test canSaveCard is false for other session types`() {
        val mockSession = mockk<AirwallexSession>(relaxed = true)
        val viewModel = createViewModel(mockSession)
        assertFalse(viewModel.canSaveCard)
    }

    @Test
    fun `one off payment method includes provided billing when information is required by session`() {
        val card: PaymentMethod.Card = mockk()
        val billing: Billing = mockk()
        val session: AirwallexPaymentSession = mockk(relaxed = true)
        every { airwallex.confirmPaymentIntent(any(), card, billing, true, any()) } just Runs
        val viewModel = createViewModel(session)
        viewModel.confirmPayment(card, true, billing)
        verify { airwallex.confirmPaymentIntent(any(), card, billing, true, any()) }
    }

    @Test
    fun `test page view tracking`() {
        val mockSession = mockk<AirwallexSession>(relaxed = true)
        val viewModel = createViewModel(
            mockSession,
            listOf(CardScheme("mastercard"), CardScheme("visa"))
        )
        assertEquals(viewModel.pageName, "card_payment_view")
        assertEquals(
            viewModel.additionalInfo, mapOf("supportedSchemes" to listOf("mastercard", "visa"))
        )
    }

    @Test
    fun `test cardHolderName returns empty when no shipping`() {
        val mockSession = mockk<AirwallexRecurringSession>(relaxed = true) {
            every { shipping } returns null
        }
        val viewModel = createViewModel(mockSession)
        assertEquals("", viewModel.cardHolderName)
    }

    @Test
    fun `test cardHolderName combines first and last name from shipping`() {
        val mockShipping = mockk<Shipping>(relaxed = true) {
            every { firstName } returns "John"
            every { lastName } returns "Doe"
        }
        val mockSession = mockk<AirwallexRecurringSession>(relaxed = true) {
            every { shipping } returns mockShipping
        }
        val viewModel = createViewModel(mockSession)
        assertEquals("John Doe", viewModel.cardHolderName)
    }

    @Test
    fun `test countryCode returns from session`() {
        val expectedCountryCode = "GB"
        val mockSession = mockk<AirwallexSession>(relaxed = true) {
            every { countryCode } returns expectedCountryCode
        }
        val viewModel = createViewModel(mockSession)
        assertEquals(expectedCountryCode, viewModel.countryCode)
    }

    @Test
    fun `test getCardNumberValidationMessage with empty input`() {
        val viewModel = createViewModel(createBasicMockSession())
        assertEquals(
            R.string.airwallex_empty_card_number, viewModel.getCardNumberValidationMessage("")
        )
    }

    @Test
    fun `test getCardNumberValidationMessage with invalid card`() {
        val viewModel = createViewModel(createBasicMockSession())
        assertEquals(
            R.string.airwallex_invalid_card_number, viewModel.getCardNumberValidationMessage("1234")
        )
    }

    @Test
    fun `test getCardNumberValidationMessage with unsupported card`() {
        val mockSession = mockk<AirwallexSession>(relaxed = true)
        val viewModel = createViewModel(mockSession, listOf(CardScheme("mastercard")))
        assertEquals(
            R.string.airwallex_unsupported_card_number,
            viewModel.getCardNumberValidationMessage(visaCardNumber)
        )
    }

    @Test
    fun `test getCardNumberValidationMessage with valid card`() {
        val mockSession = mockk<AirwallexSession>(relaxed = true)
        val viewModel = createViewModel(mockSession, listOf(CardScheme("visa")))
        assertNull(viewModel.getCardNumberValidationMessage(visaCardNumber))
    }

    @Test
    fun `test getExpiryValidationMessage with empty input`() {
        val viewModel = createViewModel(createBasicMockSession())
        every { ExpiryDateUtils.isValidExpiryDate(any()) } returns false
        assertEquals(R.string.airwallex_empty_expiry, viewModel.getExpiryValidationMessage(""))
    }

    @Test
    fun `test getExpiryValidationMessage with invalid expiry`() {
        val viewModel = createViewModel(createBasicMockSession())
        every { ExpiryDateUtils.isValidExpiryDate(any()) } returns false
        assertEquals(
            R.string.airwallex_invalid_expiry_date, viewModel.getExpiryValidationMessage("13/25")
        )
    }

    @Test
    fun `test getExpiryValidationMessage with valid expiry`() {
        val viewModel = createViewModel(createBasicMockSession())
        every { ExpiryDateUtils.isValidExpiryDate(any()) } returns true
        assertNull(viewModel.getExpiryValidationMessage("12/25"))
    }

    @Test
    fun `test getCvvValidationMessage with empty input`() {
        val viewModel = createViewModel(createBasicMockSession())
        assertEquals(
            R.string.airwallex_empty_cvc, viewModel.getCvvValidationMessage("", CardBrand.Visa)
        )
    }

    @Test
    fun `test getCvvValidationMessage with invalid CVV length`() {
        val viewModel = createViewModel(createBasicMockSession())
        assertEquals(
            R.string.airwallex_invalid_cvc, viewModel.getCvvValidationMessage("12", CardBrand.Visa)
        )
    }

    @Test
    fun `test getCvvValidationMessage with valid CVV`() {
        val viewModel = createViewModel(createBasicMockSession())
        assertNull(viewModel.getCvvValidationMessage("123", CardBrand.Visa))
        assertNull(viewModel.getCvvValidationMessage("1234", CardBrand.Amex))
    }

    @Test
    fun `test getCardHolderNameValidationMessage with empty input`() {
        val viewModel = createViewModel(createBasicMockSession())
        assertEquals(
            R.string.airwallex_empty_card_name, viewModel.getCardHolderNameValidationMessage("")
        )
    }

    @Test
    fun `test getCardHolderNameValidationMessage with valid name`() {
        val viewModel = createViewModel(createBasicMockSession())
        assertNull(viewModel.getCardHolderNameValidationMessage("John Doe"))
    }

    @Test
    fun `test getEmailValidationMessage with empty input`() {
        val mockSession = mockk<AirwallexSession>(relaxed = true)
        val viewModel = createViewModel(mockSession, isEmailRequired = true)
        assertEquals(R.string.airwallex_empty_email, viewModel.getEmailValidationMessage(""))
    }

    @Test
    fun `test getEmailValidationMessage with invalid input`() {
        val mockSession = mockk<AirwallexSession>(relaxed = true)
        val viewModel = createViewModel(mockSession, isEmailRequired = true)
        assertEquals(R.string.airwallex_invalid_email, viewModel.getEmailValidationMessage("foo"))
    }

    @Test
    fun `test getEmailValidationMessage with valid input`() {
        val mockSession = mockk<AirwallexSession>(relaxed = true)
        val viewModel = createViewModel(mockSession, isEmailRequired = true)
        assertNull(viewModel.getEmailValidationMessage("foo@email.com"))
    }

    @Test
    fun `test getBillingValidationMessage with empty input`() {
        val viewModel = createViewModel(createBasicMockSession())
        assertEquals(
            R.string.airwallex_empty_street,
            viewModel.getBillingValidationMessage(
                "",
                AddPaymentMethodViewModel.BillingFieldType.STREET,
            ),
        )
        assertEquals(
            R.string.airwallex_empty_city,
            viewModel.getBillingValidationMessage(
                "",
                AddPaymentMethodViewModel.BillingFieldType.CITY,
            ),
        )
        assertEquals(
            R.string.airwallex_empty_state,
            viewModel.getBillingValidationMessage(
                "",
                AddPaymentMethodViewModel.BillingFieldType.STATE,
            ),
        )
    }

    @Test
    fun `test getBillingValidationMessage with valid input`() {
        val viewModel = createViewModel(createBasicMockSession())
        assertNull(
            viewModel.getBillingValidationMessage(
                "123 Main St", AddPaymentMethodViewModel.BillingFieldType.STREET
            )
        )
    }

    @Test
    fun `test createCard with valid input`() {
        val viewModel = createViewModel(createBasicMockSession())
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
        val viewModel = createViewModel(createBasicMockSession())
        every { "13/25".createExpiryMonthAndYear() } returns null
        assertNull(viewModel.createCard(visaCardNumber, "John Doe", "13/25", "123"))
    }

    @Test
    fun `test createBillingWithShipping with valid input`() {
        val viewModel = createViewModel(createBasicMockSession())
        val billing = viewModel.createBilling(
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

    @Test
    fun `test confirmPayment calls airwallex with correct parameters`() {
        val session = mockk<AirwallexPaymentSession>(relaxed = true) {
            every { isBillingInformationRequired } returns true
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

    // Tests for user input state retention (added in commit b6c790b7)

    @Test
    fun `test initial card input state values`() {
        val mockShipping = mockk<Shipping>(relaxed = true) {
            every { email } returns "test@example.com"
            every { firstName } returns "John"
            every { lastName } returns "Doe"
            every { address } returns mockk(relaxed = true) {
                every { street } returns "123 Main St"
                every { city } returns "San Francisco"
                every { state } returns "CA"
                every { postcode } returns "94105"
            }
            every { phoneNumber } returns "+1234567890"
        }
        val mockSession = mockk<AirwallexPaymentSession>(relaxed = true) {
            every { shipping } returns mockShipping
            every { customerId } returns "customer123"
            every { countryCode } returns "US"
            every { isBillingInformationRequired } returns true
            every { isEmailRequired } returns true
        }

        val viewModel = createViewModel(mockSession)

        // Card input states should be initialized empty except for derived values
        assertEquals("", viewModel.cardNumber.value)
        assertEquals("", viewModel.expiryDate.value)
        assertEquals("", viewModel.cvv.value)
        assertEquals("John Doe", viewModel.cardHolderNameState.value)
        assertEquals("test@example.com", viewModel.email.value)
        assertEquals(CardBrand.Unknown, viewModel.cardBrand.value)
        assertTrue(viewModel.isSaveCardChecked.value) // true because customerId is not null

        // Billing states should be initialized with shipping values
        assertTrue(viewModel.isSameAddressChecked.value) // true because shipping is not null
        assertEquals("US", viewModel.selectedCountryCode.value)
        assertEquals("123 Main St", viewModel.street.value)
        assertEquals("CA", viewModel.state.value)
        assertEquals("San Francisco", viewModel.city.value)
        assertEquals("94105", viewModel.zipCode.value)
        assertEquals("+1234567890", viewModel.phoneNumber.value)
    }

    @Test
    fun `test initial state values with no shipping`() {
        val mockSession = mockk<AirwallexSession>(relaxed = true) {
            every { shipping } returns null
            every { countryCode } returns "US"
        }

        val viewModel =
            createViewModel(mockSession, isBillingRequired = false, isEmailRequired = false)

        // Card input states should be initialized empty
        assertEquals("", viewModel.cardNumber.value)
        assertEquals("", viewModel.expiryDate.value)
        assertEquals("", viewModel.cvv.value)
        assertEquals("", viewModel.cardHolderNameState.value)
        assertEquals("", viewModel.email.value)
        assertEquals(CardBrand.Unknown, viewModel.cardBrand.value)
        assertFalse(viewModel.isSaveCardChecked.value)

        // Billing states should be initialized empty or with defaults
        assertFalse(viewModel.isSameAddressChecked.value) // false because shipping is null
        assertEquals("US", viewModel.selectedCountryCode.value)
        assertEquals("", viewModel.street.value)
        assertEquals("", viewModel.state.value)
        assertEquals("", viewModel.city.value)
        assertEquals("", viewModel.zipCode.value)
        assertEquals("", viewModel.phoneNumber.value)
    }

    @Test
    fun `test updateCardNumber updates both card number and brand`() {
        val viewModel = createSimpleViewModelForStateTests()

        viewModel.updateCardNumber("4242424242424242", CardBrand.Visa)

        assertEquals("4242424242424242", viewModel.cardNumber.value)
        assertEquals(CardBrand.Visa, viewModel.cardBrand.value)
    }

    @Test
    fun `test updateExpiryDate updates expiry date state`() {
        val viewModel = createSimpleViewModelForStateTests()

        viewModel.updateExpiryDate("12/25")

        assertEquals("12/25", viewModel.expiryDate.value)
    }

    @Test
    fun `test updateCvv updates cvv state`() {
        val viewModel = createSimpleViewModelForStateTests()

        viewModel.updateCvv("123")

        assertEquals("123", viewModel.cvv.value)
    }

    @Test
    fun `test updateCardHolderName updates card holder name state`() {
        val viewModel = createSimpleViewModelForStateTests()

        viewModel.updateCardHolderName("Jane Smith")

        assertEquals("Jane Smith", viewModel.cardHolderNameState.value)
    }

    @Test
    fun `test updateEmail updates email state`() {
        val viewModel = createSimpleViewModelForStateTests()

        viewModel.updateEmail("newemail@example.com")

        assertEquals("newemail@example.com", viewModel.email.value)
    }

    @Test
    fun `test updateSaveCardChecked updates save card state`() {
        val viewModel = createSimpleViewModelForStateTests()

        viewModel.updateSaveCardChecked(true)

        assertTrue(viewModel.isSaveCardChecked.value)

        viewModel.updateSaveCardChecked(false)

        assertFalse(viewModel.isSaveCardChecked.value)
    }

    @Test
    fun `test updateSameAddressChecked updates same address state`() {
        val viewModel = createSimpleViewModelForStateTests()

        viewModel.updateSameAddressChecked(true)

        assertTrue(viewModel.isSameAddressChecked.value)

        viewModel.updateSameAddressChecked(false)

        assertFalse(viewModel.isSameAddressChecked.value)
    }

    @Test
    fun `test updateSelectedCountryCode updates country code state`() {
        val viewModel = createSimpleViewModelForStateTests()

        viewModel.updateSelectedCountryCode("CA")

        assertEquals("CA", viewModel.selectedCountryCode.value)
    }

    @Test
    fun `test updateStreet updates street state`() {
        val viewModel = createSimpleViewModelForStateTests()

        viewModel.updateStreet("456 Oak Ave")

        assertEquals("456 Oak Ave", viewModel.street.value)
    }

    @Test
    fun `test updateState updates state value`() {
        val viewModel = createSimpleViewModelForStateTests()

        viewModel.updateState("NY")

        assertEquals("NY", viewModel.state.value)
    }

    @Test
    fun `test updateCity updates city state`() {
        val viewModel = createSimpleViewModelForStateTests()

        viewModel.updateCity("New York")

        assertEquals("New York", viewModel.city.value)
    }

    @Test
    fun `test updateZipCode updates zip code state`() {
        val viewModel = createSimpleViewModelForStateTests()

        viewModel.updateZipCode("10001")

        assertEquals("10001", viewModel.zipCode.value)
    }

    @Test
    fun `test updatePhoneNumber updates phone number state`() {
        val viewModel = createSimpleViewModelForStateTests()

        viewModel.updatePhoneNumber("+1987654321")

        assertEquals("+1987654321", viewModel.phoneNumber.value)
    }

    @Test
    fun `test multiple card input updates maintain state independently`() {
        val viewModel = createSimpleViewModelForStateTests()

        viewModel.updateCardNumber("4111111111111111", CardBrand.Visa)
        viewModel.updateExpiryDate("01/28")
        viewModel.updateCvv("456")
        viewModel.updateCardHolderName("Bob Johnson")
        viewModel.updateEmail("bob@example.com")

        assertEquals("4111111111111111", viewModel.cardNumber.value)
        assertEquals("01/28", viewModel.expiryDate.value)
        assertEquals("456", viewModel.cvv.value)
        assertEquals("Bob Johnson", viewModel.cardHolderNameState.value)
        assertEquals("bob@example.com", viewModel.email.value)
        assertEquals(CardBrand.Visa, viewModel.cardBrand.value)
    }

    @Test
    fun `test multiple billing input updates maintain state independently`() {
        val viewModel = createSimpleViewModelForStateTests()

        viewModel.updateSelectedCountryCode("FR")
        viewModel.updateStreet("789 Rue de la Paix")
        viewModel.updateState("Île-de-France")
        viewModel.updateCity("Paris")
        viewModel.updateZipCode("75001")
        viewModel.updatePhoneNumber("+33123456789")
        viewModel.updateSameAddressChecked(false)

        assertEquals("FR", viewModel.selectedCountryCode.value)
        assertEquals("789 Rue de la Paix", viewModel.street.value)
        assertEquals("Île-de-France", viewModel.state.value)
        assertEquals("Paris", viewModel.city.value)
        assertEquals("75001", viewModel.zipCode.value)
        assertEquals("+33123456789", viewModel.phoneNumber.value)
        assertFalse(viewModel.isSameAddressChecked.value)
    }

    @Test
    fun `test state flows emit new values when updated`() {
        val viewModel = createSimpleViewModelForStateTests()

        // Test that StateFlows are reactive
        val cardNumberValues = mutableListOf<String>()
        val emailValues = mutableListOf<String>()

        // This is a simple test to verify StateFlow behavior
        cardNumberValues.add(viewModel.cardNumber.value)
        emailValues.add(viewModel.email.value)

        viewModel.updateCardNumber("1234567890123456", CardBrand.MasterCard)
        viewModel.updateEmail("updated@example.com")

        cardNumberValues.add(viewModel.cardNumber.value)
        emailValues.add(viewModel.email.value)

        assertEquals(listOf("", "1234567890123456"), cardNumberValues)
        assertEquals(listOf("", "updated@example.com"), emailValues)
    }

    private fun createViewModel(
        session: AirwallexSession,
        cardSchemes: List<CardScheme> = listOf(),
        isBillingRequired: Boolean = false,
        isEmailRequired: Boolean = false
    ): AddPaymentMethodViewModel {
        // Set up mock defaults before creating ViewModel
        every { session.isBillingInformationRequired } returns isBillingRequired
        every { session.isEmailRequired } returns isEmailRequired
        every { application.getString(R.string.airwallex_confirm) } returns "Confirm"
        every { application.getString(R.string.airwallex_pay_now) } returns "Pay"

        return AddPaymentMethodViewModel.Factory(application, airwallex, session, cardSchemes)
            .create(AddPaymentMethodViewModel::class.java)
    }

    private fun createSimpleViewModelForStateTests(): AddPaymentMethodViewModel {
        val mockSession = mockk<AirwallexSession>(relaxed = true) {
            every { shipping } returns null
            every { countryCode } returns "US"
        }
        return createViewModel(mockSession)
    }

    private fun createBasicMockSession(): AirwallexSession {
        return mockk(relaxed = true) {
            every { countryCode } returns "US"
        }
    }
}
