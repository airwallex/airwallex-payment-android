package com.airwallex.android.view

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.ClientSecretRepository
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.exception.InvalidParamsException
import com.airwallex.android.core.model.*
import com.airwallex.android.R

internal class AddPaymentMethodViewModel(
    application: Application,
    private val airwallex: Airwallex,
    private val session: AirwallexSession,
    private val supportedCardSchemes: List<CardScheme>
) : AndroidViewModel(application) {

    fun getValidationResult(cardNumber: String): ValidationResult {
        if (cardNumber.isEmpty()) {
            return ValidationResult.Error(R.string.airwallex_empty_card_number)
        }
        if (CardUtils.isValidCardNumber(cardNumber)) {
            val cardBrand = CardUtils.getPossibleCardBrand(cardNumber, true)
            return if (supportedCardSchemes.map { CardBrand.fromType(it.name) }.contains(cardBrand)) {
                return ValidationResult.Success
            } else {
                return ValidationResult.Error(R.string.airwallex_unsupported_card_number)
            }
        }
        return ValidationResult.Error(R.string.airwallex_invalid_card_number)
    }

    fun createPaymentMethod(
        card: PaymentMethod.Card,
        shouldStoreCard: Boolean,
        billing: Billing?
    ): LiveData<PaymentMethodResult> {
        val resolvedBilling = if (session.isBillingInformationRequired) billing else null
        return if (session is AirwallexPaymentSession && !shouldStoreCard) {
            createOneOffCardPaymentMethod(card, resolvedBilling)
        } else {
            createStoredCardPaymentMethod(card, resolvedBilling)
        }
    }

    private fun createOneOffCardPaymentMethod(
        card: PaymentMethod.Card,
        billing: Billing?
    ): LiveData<PaymentMethodResult> {
        val resultData = MutableLiveData<PaymentMethodResult>()

        try {
            resultData.value = PaymentMethodResult.Success(
                PaymentMethod.Builder()
                    .setType(PaymentMethodType.CARD.value)
                    .setCard(card)
                    .setBilling(billing)
                    .build(),
                requireNotNull(card.cvc)
            )
        } catch (e: IllegalArgumentException) {
            val exception = InvalidParamsException("Card CVC missing")
            resultData.value = PaymentMethodResult.Error(exception)
        }

        return resultData
    }

    private fun createStoredCardPaymentMethod(
        card: PaymentMethod.Card,
        billing: Billing?,
    ): LiveData<PaymentMethodResult> {
        val resultData = MutableLiveData<PaymentMethodResult>()

        try {
            ClientSecretRepository.getInstance().retrieveClientSecret(
                requireNotNull(session.customerId),
                object : ClientSecretRepository.ClientSecretRetrieveListener {
                    override fun onClientSecretRetrieve(clientSecret: ClientSecret) {
                        airwallex.createPaymentMethod(
                            CreatePaymentMethodParams(
                                clientSecret = clientSecret.value,
                                customerId = requireNotNull(session.customerId),
                                card = card,
                                billing = billing
                            ),
                            object : Airwallex.PaymentListener<PaymentMethod> {
                                override fun onSuccess(response: PaymentMethod) {
                                    resultData.value = PaymentMethodResult.Success(response, requireNotNull(card.cvc))
                                }

                                override fun onFailed(exception: AirwallexException) {
                                    resultData.value = PaymentMethodResult.Error(exception)
                                }
                            }
                        )
                    }

                    override fun onClientSecretError(errorMessage: String) {
                        resultData.value =
                            PaymentMethodResult.Error(AirwallexCheckoutException(message = errorMessage))
                    }
                }
            )
        } catch (e: AirwallexCheckoutException) {
            resultData.value = PaymentMethodResult.Error(e)
        }

        return resultData
    }

    sealed class PaymentMethodResult {
        data class Success(val paymentMethod: PaymentMethod, val cvc: String) :
            PaymentMethodResult()

        data class Error(val exception: AirwallexException) : PaymentMethodResult()
    }

    internal class Factory(
        private val application: Application,
        private val airwallex: Airwallex,
        private val session: AirwallexSession,
        private val supportedCardSchemes: List<CardScheme>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
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
