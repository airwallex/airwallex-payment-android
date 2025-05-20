package com.airwallex.android.view

import android.app.Application
import android.util.Patterns
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
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
import com.airwallex.android.view.util.ExpiryDateUtils
import com.airwallex.android.view.util.createExpiryMonthAndYear
import com.airwallex.android.view.util.isValidCvc
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddPaymentMethodViewModel(
    application: Application,
    private val airwallex: Airwallex,
    private val session: AirwallexSession,
    private val supportedCardSchemes: List<CardScheme>
) : AndroidViewModel(application) {
    val pageName: String = "card_payment_view"
    val additionalInfo: Map<String, List<String>> =
        mapOf("supportedSchemes" to supportedCardSchemes.map { it.name })

    val ctaTitle = if (session is AirwallexRecurringSession) {
        application.getString(R.string.airwallex_confirm)
    } else {
        application.getString(R.string.airwallex_pay_now)
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
            listOfNotNull(shipping?.firstName, shipping?.lastName)
                .joinToString(" ")
                .ifEmpty { "" }
        }
    }

    val countryCode: String by lazy { session.countryCode }

    private val _airwallexPaymentStatus = MutableLiveData<AirwallexPaymentStatus>()
    val airwallexPaymentStatus: LiveData<AirwallexPaymentStatus> = _airwallexPaymentStatus

    private val _deleteCardEvent = MutableStateFlow<PaymentConsent?>(null)
    val deleteCardEvent: StateFlow<PaymentConsent?> = _deleteCardEvent.asStateFlow()

    fun getValidationResult(cardNumber: String): ValidationResult {
        if (cardNumber.isEmpty()) {
            return ValidationResult.Error(R.string.airwallex_empty_card_number)
        }
        if (CardUtils.isValidCardNumber(cardNumber)) {
            val cardBrand = CardUtils.getPossibleCardBrand(cardNumber, true)
            val supportedCardBrands = supportedCardSchemes.map { CardBrand.fromType(it.name) }
            return if (supportedCardBrands.contains(cardBrand)) {
                ValidationResult.Success
            } else {
                ValidationResult.Error(R.string.airwallex_unsupported_card_number)
            }
        }
        return ValidationResult.Error(R.string.airwallex_invalid_card_number)
    }

    fun getCardNumberValidationMessage(cardNumber: String): Int? {
        return when {
            cardNumber.isBlank() -> R.string.airwallex_empty_card_number
            !CardUtils.isValidCardNumber(cardNumber) -> R.string.airwallex_invalid_card_number
            supportedCardSchemes.none { CardBrand.fromType(it.name) == CardUtils.getPossibleCardBrand(cardNumber, true) } -> R.string.airwallex_unsupported_card_number
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
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> R.string.airwallex_invalid_email
            else -> null
        }
    }

    fun getBillingValidationMessage(input: String, type: BillingFieldType): Int? {
        return when {
            input.isBlank() -> when (type) {
                BillingFieldType.STREET -> type.errorMessage
                BillingFieldType.CITY -> type.errorMessage
                BillingFieldType.STATE -> type.errorMessage
                BillingFieldType.POSTAL_CODE -> type.errorMessage
                BillingFieldType.PONE_NUMBER -> type.errorMessage
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
                    _airwallexPaymentStatus.value = status
                }
            },
        )
    }

    fun createCard(cardNumber: String, name: String, expiryDate: String, cvv: String): PaymentMethod.Card? {
        if (cardNumber.isBlank() || name.isBlank() || expiryDate.isBlank() || cvv.isBlank()) {
            return null
        }
        val (month, year) = expiryDate.createExpiryMonthAndYear() ?: return null
        return PaymentMethod.Card.Builder()
            .setNumber(CardUtils.removeSpacesAndHyphens(cardNumber))
            .setName(name.trim())
            .setExpiryMonth(if (month < 10) "0$month" else month.toString())
            .setExpiryYear(year.toString())
            .setCvc(cvv.trim())
            .build()
    }

    fun createBillingWithShipping(countryCode: String, state: String, city: String, street: String, postcode: String, phoneNumber: String, email: String): Billing {
        return Billing.Builder()
            .setAddress(
                Address.Builder()
                    .setCountryCode(countryCode)
                    .setState(state)
                    .setCity(city)
                    .setStreet(street)
                    .setPostcode(postcode)
                    .build()
            )
            .setPhone(phoneNumber)
            .setEmail(email)
            .build()
    }

    fun deleteCard(consent: PaymentConsent) {
        _deleteCardEvent.update { consent }
    }

    fun isCvcRequired(paymentConsent: PaymentConsent) = paymentConsent.paymentMethod?.card?.numberType == PaymentMethod.Card.NumberType.PAN

    internal class Factory(
        private val application: Application,
        private val airwallex: Airwallex,
        private val session: AirwallexSession,
        private val supportedCardSchemes: List<CardScheme>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AddPaymentMethodViewModel(
                application, airwallex, session, supportedCardSchemes
            ) as T
        }
    }

    sealed class ValidationResult {
        object Success : ValidationResult()
        data class Error(@StringRes val message: Int) : ValidationResult()
    }

    enum class BillingFieldType(@StringRes val errorMessage: Int) {
        STREET(R.string.airwallex_empty_street),
        CITY(R.string.airwallex_empty_city),
        STATE(R.string.airwallex_empty_state),
        POSTAL_CODE(R.string.airwallex_empty_postal_code),
        PONE_NUMBER(R.string.airwallex_empty_phone_number),
    }
}
