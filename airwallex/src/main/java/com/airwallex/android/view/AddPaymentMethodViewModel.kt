package com.airwallex.android.view

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.airwallex.android.R
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexRecurringSession
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.CardBrand
import com.airwallex.android.core.model.Address
import com.airwallex.android.core.model.Billing
import com.airwallex.android.core.model.CardScheme
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.Shipping
import com.airwallex.android.core.util.CardUtils
import com.airwallex.android.ui.checkout.AirwallexCheckoutViewModel
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
    val additionalInfo: Map<String, List<String>> =
        mapOf("supportedSchemes" to supportedCardSchemes.map { it.name })

    @StringRes
    val ctaRes: Int = if (session is AirwallexRecurringSession) {
        R.string.airwallex_confirm
    } else {
        R.string.airwallex_pay_now
    }

    val shipping: Shipping? by lazy {
        session.shipping
    }

    val canSaveCard: Boolean by lazy { session is AirwallexPaymentSession && session.customerId != null }

    val isBillingRequired: Boolean by lazy { session.isBillingInformationRequired }

    val isEmailRequired: Boolean by lazy { session.isEmailRequired }

    val cardHolderName: String by lazy {
        if (shipping == null) {
            ""
        } else {
            listOfNotNull(shipping?.firstName, shipping?.lastName).joinToString(" ").ifEmpty { "" }
        }
    }

    val countryCode: String by lazy { session.countryCode }

    private val _airwallexPaymentStatus = MutableLiveData<AirwallexPaymentStatus>()
    val airwallexPaymentStatus: LiveData<AirwallexPaymentStatus> = _airwallexPaymentStatus

    private val _deleteCardSuccess = MutableStateFlow<PaymentConsent?>(null)
    val deleteCardSuccess: StateFlow<PaymentConsent?> = _deleteCardSuccess.asStateFlow()

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
    private val _isSameAddressChecked = MutableStateFlow(shipping != null)
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
            supportedCardSchemes.none {
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
        return when {
            input.isBlank() -> when (type) {
                BillingFieldType.STREET -> type.errorMessage
                BillingFieldType.CITY -> type.errorMessage
                BillingFieldType.STATE -> type.errorMessage
                else -> null
            }

            else -> null
        }
    }

    fun confirmPayment(card: PaymentMethod.Card, saveCard: Boolean, billing: Billing?) {
        airwallex.confirmPaymentIntent(
            session = session,
            card = card,
            billing = billing,
            saveCard = saveCard,
            listener = object : Airwallex.PaymentResultListener {
                override fun onCompleted(status: AirwallexPaymentStatus) {
                    _airwallexPaymentStatus.postValue(status)
                }
            },
        )
    }

    /**
     * Confirm payment with callback - allows caller to handle result directly
     */
    fun confirmPayment(
        card: PaymentMethod.Card,
        saveCard: Boolean,
        billing: Billing?,
        onResult: (AirwallexPaymentStatus) -> Unit
    ) {
        airwallex.confirmPaymentIntent(
            session = session,
            card = card,
            billing = billing,
            saveCard = saveCard,
            listener = object : Airwallex.PaymentResultListener {
                override fun onCompleted(status: AirwallexPaymentStatus) {
                    onResult(status)
                }
            },
        )
    }

    fun createCard(
        cardNumber: String,
        name: String,
        expiryDate: String,
        cvv: String,
    ): PaymentMethod.Card? {
        if (cardNumber.isBlank() || name.isBlank() || expiryDate.isBlank() || cvv.isBlank()) {
            return null
        }
        val (month, year) = expiryDate.createExpiryMonthAndYear() ?: return null
        return PaymentMethod.Card.Builder().setNumber(CardUtils.removeSpacesAndHyphens(cardNumber))
            .setName(name.trim()).setExpiryMonth(if (month < 10) "0$month" else month.toString())
            .setExpiryYear(year.toString()).setCvc(cvv.trim()).build()
    }

    fun createBilling(
        countryCode: String,
        state: String,
        city: String,
        street: String,
        postcode: String,
        phoneNumber: String,
        email: String
    ): Billing {
        return Billing.Builder().setAddress(
                Address.Builder().setCountryCode(countryCode).setState(state).setCity(city)
                    .setStreet(street).setPostcode(postcode).build()
            ).setPhone(phoneNumber).setEmail(email).build()
    }

    fun deleteCardSuccess(consent: PaymentConsent) {
        _deleteCardSuccess.update { consent }
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
        STREET(R.string.airwallex_empty_street),
        CITY(R.string.airwallex_empty_city),
        STATE(R.string.airwallex_empty_state)
    }
}
