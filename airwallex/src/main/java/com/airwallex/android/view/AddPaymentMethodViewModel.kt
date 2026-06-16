package com.airwallex.android.view

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.airwallex.android.R
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.AirwallexRecurringSession
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.CardBrand
import com.airwallex.android.core.RequiredBillingContactField
import com.airwallex.android.core.Session
import com.airwallex.android.core.model.Address
import com.airwallex.android.core.model.Billing
import com.airwallex.android.core.model.CardScheme
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.Shipping
import com.airwallex.android.core.resolvedRequiredBillingContactFields
import com.airwallex.android.core.util.CardUtils
import com.airwallex.android.core.util.isValidE164Phone
import com.airwallex.android.ui.checkout.AirwallexCheckoutViewModel
import com.airwallex.android.core.util.AddressSpec
import com.airwallex.android.view.util.ExpiryDateUtils
import com.airwallex.android.view.util.createExpiryMonthAndYear
import com.airwallex.android.view.util.isValidCvc
import com.airwallex.android.view.util.isValidEmail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Suppress("ComplexCondition", "LongParameterList")
class AddPaymentMethodViewModel(
    application: Application,
    airwallex: Airwallex,
    private val session: AirwallexSession,
    private val supportedCardSchemes: List<CardScheme>
) : AirwallexCheckoutViewModel(application, airwallex, session) {
    val pageName: String = "card_payment_view"

    private var cardSchemes = supportedCardSchemes

    fun updateSupportedCardSchemes(schemes: List<CardScheme>) {
        cardSchemes = schemes
    }

    val additionalInfo: Map<String, List<String>>
        get() = mapOf("supportedSchemes" to cardSchemes.map { it.name })

    @StringRes
    val ctaRes: Int = if (session is AirwallexRecurringSession) {
        R.string.airwallex_confirm
    } else {
        R.string.airwallex_pay_now
    }

    val shipping: Shipping? by lazy {
        session.shipping
    }

    val canSaveCard: Boolean by lazy { (session is AirwallexPaymentSession || (session is Session && session.isOneOffPayment)) && !session.customerId.isNullOrEmpty() }

    /**
     * Resolved (non-null) set of billing-contact fields to render on this screen.
     * Distinct from [AirwallexSession.requiredBillingContactFields] which is the
     * raw nullable merchant configuration; here we've already collapsed the
     * "null → derive from legacy booleans" rule via [resolvedRequiredBillingContactFields].
     */
    val resolvedBillingFields: Set<RequiredBillingContactField> by lazy {
        session.resolvedRequiredBillingContactFields
    }

    val showName: Boolean by lazy {
        RequiredBillingContactField.NAME in resolvedBillingFields
    }
    val showEmail: Boolean by lazy {
        RequiredBillingContactField.EMAIL in resolvedBillingFields
    }
    val showPhone: Boolean by lazy {
        RequiredBillingContactField.PHONE in resolvedBillingFields
    }
    val showAddress: Boolean by lazy {
        RequiredBillingContactField.ADDRESS in resolvedBillingFields
    }
    /** Country picker without the rest of the address. ADDRESS suppresses this. */
    val showCountryCodeOnly: Boolean by lazy {
        !showAddress && RequiredBillingContactField.COUNTRY_CODE in resolvedBillingFields
    }
    /** Whether the "Billing information" header / section block should appear. */
    val showBillingSection: Boolean by lazy {
        showAddress || showCountryCodeOnly
    }
    /** Whether the "Same as shipping" prefill toggle should appear. */
    val showSameAsShippingToggle: Boolean by lazy {
        showAddress && shipping != null
    }

    val cardHolderName: String by lazy {
        if (shipping == null) {
            ""
        } else {
            listOfNotNull(shipping?.firstName, shipping?.lastName).joinToString(" ").ifEmpty { "" }
        }
    }

    val countryCode: String by lazy { session.countryCode }

    private val _deletedCardList = MutableStateFlow<MutableList<PaymentConsent>>(mutableListOf())
    val deletedCardList: StateFlow<MutableList<PaymentConsent>> = _deletedCardList.asStateFlow()

    // Card input state
    private val _cardNumber = MutableStateFlow("")
    val cardNumber: StateFlow<String> = _cardNumber.asStateFlow()

    private val _expiryDate = MutableStateFlow("")
    val expiryDate: StateFlow<String> = _expiryDate.asStateFlow()

    private val _cvv = MutableStateFlow("")
    val cvv: StateFlow<String> = _cvv.asStateFlow()

    private val _cardHolderName = MutableStateFlow(cardHolderName)
    val cardHolderNameState: StateFlow<String> = _cardHolderName.asStateFlow()

    private val _email = MutableStateFlow(shipping?.email.orEmpty())
    val email: StateFlow<String> = _email.asStateFlow()

    private val _cardBrand = MutableStateFlow(CardBrand.Unknown)
    val cardBrand: StateFlow<CardBrand> = _cardBrand.asStateFlow()

    private val _isSaveCardChecked = MutableStateFlow(canSaveCard)
    val isSaveCardChecked: StateFlow<Boolean> = _isSaveCardChecked.asStateFlow()

    // Billing state
    private val _isSameAddressChecked = MutableStateFlow(showSameAsShippingToggle)
    val isSameAddressChecked: StateFlow<Boolean> = _isSameAddressChecked.asStateFlow()

    private val _selectedCountryCode = MutableStateFlow(countryCode)
    val selectedCountryCode: StateFlow<String> = _selectedCountryCode.asStateFlow()

    private val _street = MutableStateFlow(shipping?.address?.street.orEmpty())
    val street: StateFlow<String> = _street.asStateFlow()

    private val _state = MutableStateFlow(shipping?.address?.state.orEmpty())
    val state: StateFlow<String> = _state.asStateFlow()

    private val _city = MutableStateFlow(shipping?.address?.city.orEmpty())
    val city: StateFlow<String> = _city.asStateFlow()

    private val _zipCode = MutableStateFlow(shipping?.address?.postcode.orEmpty())
    val zipCode: StateFlow<String> = _zipCode.asStateFlow()

    private val _phoneNumber = MutableStateFlow(shipping?.phoneNumber.orEmpty())
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    // Update functions
    fun updateCardNumber(value: String, brand: CardBrand) {
        _cardNumber.value = value
        _cardBrand.value = brand
    }

    fun updateExpiryDate(value: String) {
        _expiryDate.value = value
    }

    fun updateCvv(value: String) {
        _cvv.value = value
    }

    fun updateCardHolderName(value: String) {
        _cardHolderName.value = value
    }

    fun updateEmail(value: String) {
        _email.value = value
    }

    fun updateSaveCardChecked(checked: Boolean) {
        _isSaveCardChecked.value = checked
    }

    fun updateSameAddressChecked(checked: Boolean) {
        _isSameAddressChecked.value = checked
    }

    fun updateSelectedCountryCode(value: String) {
        _selectedCountryCode.value = value
    }

    fun updateStreet(value: String) {
        _street.value = value
    }

    fun updateState(value: String) {
        _state.value = value
    }

    fun updateCity(value: String) {
        _city.value = value
    }

    fun updateZipCode(value: String) {
        _zipCode.value = value
    }

    fun updatePhoneNumber(value: String) {
        _phoneNumber.value = value
    }

    fun getCardNumberValidationMessage(cardNumber: String): Int? {
        return when {
            cardNumber.isBlank() -> R.string.airwallex_empty_card_number
            !CardUtils.isValidCardNumber(cardNumber) -> R.string.airwallex_invalid_card_number
            cardSchemes.none {
                CardBrand.fromType(it.name) == CardUtils.getPossibleCardBrand(
                    cardNumber, true
                )
            } -> R.string.airwallex_unsupported_card_number

            else -> null
        }
    }

    fun getExpiryValidationMessage(expiryDate: String): Int? {
        return when {
            expiryDate.isBlank() -> R.string.airwallex_empty_expiry
            !ExpiryDateUtils.isValidExpiryDate(expiryDate) -> R.string.airwallex_invalid_expiry_date
            else -> null
        }
    }

    fun getCvvValidationMessage(cvv: String, brand: CardBrand): Int? {
        return when {
            cvv.isBlank() -> R.string.airwallex_empty_cvc
            !cvv.isValidCvc(brand) -> R.string.airwallex_invalid_cvc
            else -> null
        }
    }

    fun getCardHolderNameValidationMessage(cardHolderName: String): Int? {
        return when {
            cardHolderName.isBlank() -> R.string.airwallex_empty_card_name
            else -> null
        }
    }

    fun getEmailValidationMessage(email: String): Int? {
        return when {
            email.isBlank() -> R.string.airwallex_empty_email
            !email.isValidEmail() -> R.string.airwallex_invalid_email
            else -> null
        }
    }

    fun getBillingValidationMessage(input: String, type: BillingFieldType): Int? {
        return when (type) {
            BillingFieldType.STREET,
            BillingFieldType.STATE,
            BillingFieldType.CITY,
            BillingFieldType.POSTCODE,
            BillingFieldType.COUNTRY_CODE -> if (input.isBlank()) type.errorMessage else null

            BillingFieldType.PHONE -> when {
                input.isBlank() -> R.string.airwallex_empty_phone
                !input.isValidE164Phone() -> R.string.airwallex_invalid_phone
                else -> null
            }
        }
    }

    /**
     * Postcode validation: presence + country-specific regex.
     *
     * Any field that is visible is required, so callers are expected to gate on
     * [AddressSpec.hasPostcode] before invoking — by the time we get here, the field is
     * being collected and must be non-blank.
     *
     * Returns:
     *  - [R.string.airwallex_required] when blank
     *  - [R.string.airwallex_please_enter_valid_value] when non-blank but the country's pattern doesn't match
     *  - null when valid (or non-blank for a country with no declared pattern)
     */
    @StringRes
    fun getPostcodeValidationMessage(input: String, countryCode: String): Int? {
        if (input.isBlank()) return R.string.airwallex_required
        val pattern = AddressSpec.postcodePattern(countryCode) ?: return null
        return if (pattern.matches(input.trim())) null else R.string.airwallex_please_enter_valid_value
    }

    fun createCard(
        cardNumber: String,
        name: String,
        expiryDate: String,
        cvv: String,
    ): PaymentMethod.Card? {
        if (cardNumber.isBlank() || expiryDate.isBlank() || cvv.isBlank()) {
            return null
        }
        if (showName && name.isBlank()) return null
        val (month, year) = expiryDate.createExpiryMonthAndYear() ?: return null
        val builder = PaymentMethod.Card.Builder()
            .setNumber(CardUtils.removeSpacesAndHyphens(cardNumber))
            .setExpiryMonth(if (month < 10) "0$month" else month.toString())
            .setExpiryYear(year.toString())
            .setCvc(cvv.trim())
        if (showName) builder.setName(name.trim())
        return builder.build()
    }

    /**
     * Build a [Billing] containing only the fields the merchant asked for via
     * [resolvedBillingFields]. Returns `null` when the set is empty so no
     * billing payload is sent.
     */
    fun createBilling(
        name: String,
        email: String,
        phoneNumber: String,
        countryCode: String,
        state: String,
        city: String,
        street: String,
        postcode: String,
    ): Billing? {
        if (resolvedBillingFields.isEmpty()) return null

        val builder = Billing.Builder()

        if (showName) {
            val parts = name.trim().split(' ', limit = 2)
            builder.setFirstName(parts[0])
            builder.setLastName(parts.getOrNull(1).orEmpty())
        }
        if (showEmail) builder.setEmail(email)
        if (showPhone) builder.setPhone(phoneNumber)

        when {
            showAddress -> {
                // Mirror the visibility rules from [AddressSpec] in the payload so we never
                // send a value the country's address spec doesn't collect (and which the user
                // never saw a field for — e.g. a stale city when switching from US to JP).
                val addressBuilder = Address.Builder()
                    .setCountryCode(countryCode)
                    .setStreet(street)
                if (AddressSpec.hasState(countryCode)) addressBuilder.setState(state)
                if (AddressSpec.hasCity(countryCode)) addressBuilder.setCity(city)
                if (AddressSpec.hasPostcode(countryCode)) addressBuilder.setPostcode(postcode)
                builder.setAddress(addressBuilder.build())
            }
            showCountryCodeOnly -> builder.setAddress(
                Address.Builder().setCountryCode(countryCode).build()
            )
        }

        return builder.build()
    }

    fun deleteCardSuccess(consent: PaymentConsent) {
        _deletedCardList.update { currentList ->
            val newList = currentList.toMutableList()
            newList.add(consent)
            newList
        }
    }

    fun isCvcRequired(paymentConsent: PaymentConsent) =
        paymentConsent.paymentMethod?.card?.numberType == PaymentMethod.Card.NumberType.PAN

    internal class Factory(
        private val application: Application,
        private val airwallex: Airwallex,
        private val session: AirwallexSession,
        private val supportedCardSchemes: List<CardScheme>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddPaymentMethodViewModel(
                application = application,
                airwallex = airwallex,
                session = session,
                supportedCardSchemes = supportedCardSchemes
            ) as T
        }
    }

    enum class BillingFieldType(@StringRes val errorMessage: Int) {
        STREET(R.string.airwallex_required),
        STATE(R.string.airwallex_required),
        CITY(R.string.airwallex_required),
        POSTCODE(R.string.airwallex_required),
        PHONE(R.string.airwallex_empty_phone),
        COUNTRY_CODE(R.string.airwallex_empty_country_code),
    }
}