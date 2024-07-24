package com.airwallex.android.view

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.exception.InvalidParamsException
import com.airwallex.android.core.model.*
import com.airwallex.android.R
import com.airwallex.android.core.*
import com.airwallex.android.view.util.CardUtils
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class AddPaymentMethodViewModel(
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
        when (session) {
            is AirwallexPaymentSession -> {
                session.paymentIntent.order?.shipping
            }

            is AirwallexRecurringWithIntentSession -> {
                session.paymentIntent.order?.shipping
            }

            is AirwallexRecurringSession -> {
                session.shipping
            }

            else -> null
        }
    }

    val canSaveCard: Boolean by lazy { session is AirwallexPaymentSession && session.customerId != null }

    val isBillingRequired: Boolean by lazy { session.isBillingInformationRequired }

    val isEmailRequired: Boolean by lazy { session.isEmailRequired }

    private val _airwallexPaymentStatus = MutableLiveData<AirwallexPaymentStatus>()
    val airwallexPaymentStatus: LiveData<AirwallexPaymentStatus> = _airwallexPaymentStatus

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

    fun confirmPayment(card: PaymentMethod.Card, saveCard: Boolean, billing: Billing?) {
        airwallex.confirmPaymentIntent(
            session = session,
            card,
            billing = billing,
            saveCard = saveCard,
            listener = object : Airwallex.PaymentResultListener {
                override fun onCompleted(status: AirwallexPaymentStatus) {
                    _airwallexPaymentStatus.value = status
                }
            }
        )
    }

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
}
