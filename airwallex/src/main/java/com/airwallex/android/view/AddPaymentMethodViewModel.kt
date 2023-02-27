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

    fun createPaymentMethod(
        card: PaymentMethod.Card,
        billing: Billing?
    ): LiveData<PaymentMethodResult> {
        val resolvedBilling = if (session.isBillingInformationRequired) billing else null
        return if (session is AirwallexPaymentSession) {
            createOneOffCardPaymentMethod(card, resolvedBilling)
        } else {
            createStoredCardPaymentMethod(card, resolvedBilling)
        }
    }

    suspend fun checkoutWithSavedCard(
        card: PaymentMethod.Card,
        billing: Billing?
    ): AirwallexPaymentStatus {
        val billingOrNull = if (session.isBillingInformationRequired) billing else null
        val oneOffPaymentMethod = PaymentMethod.Builder()
            .setType(PaymentMethodType.CARD.value)
            .setCard(card)
            .setBilling(billingOrNull)
            .build()
        val customerId = requireNotNull(session.customerId)
        val clientSecret = try {
            ClientSecretRepository.getInstance().retrieveClientSecret(customerId)
        } catch (e: AirwallexCheckoutException) {
            return AirwallexPaymentStatus.Failure(e)
        }
        val paymentMethod = try {
            airwallex.createPaymentMethod(
                CreatePaymentMethodParams(
                    clientSecret = clientSecret.value,
                    customerId = customerId,
                    card = card,
                    billing = billingOrNull
                )
            )
        } catch (_: Throwable) {
            oneOffPaymentMethod
        }
        try {
            val consent = airwallex.createPaymentConsent(
                CreatePaymentConsentParams.createCardParams(
                    clientSecret = clientSecret.value,
                    customerId = customerId,
                    paymentMethodId = requireNotNull(paymentMethod.id),
                    nextTriggeredBy = PaymentConsent.NextTriggeredBy.CUSTOMER,
                    requiresCvc = true,
                    merchantTriggerReason = null
                )
            )
            return suspendCoroutine { cont ->
                airwallex.checkout(
                    session = session,
                    paymentMethod = paymentMethod,
                    paymentConsentId = consent.id,
                    cvc = card.cvc,
                    listener = object : Airwallex.PaymentResultListener {
                        override fun onCompleted(status: AirwallexPaymentStatus) {
                            cont.resume(status)
                        }
                    }
                )
            }
        } catch (_: Throwable) {
            return suspendCoroutine { cont ->
                airwallex.checkout(
                    session = session,
                    paymentMethod = oneOffPaymentMethod,
                    cvc = card.cvc,
                    listener = object : Airwallex.PaymentResultListener {
                        override fun onCompleted(status: AirwallexPaymentStatus) {
                            cont.resume(status)
                        }
                    }
                )
            }
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
