package com.airwallex.android.view

import android.app.Application
import androidx.lifecycle.*
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.ClientSecretRepository
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.model.Billing
import com.airwallex.android.core.model.ClientSecret
import com.airwallex.android.core.model.CreatePaymentMethodParams
import com.airwallex.android.core.model.PaymentMethod

internal class AddPaymentMethodViewModel(
    application: Application,
    private val airwallex: Airwallex,
    private val session: AirwallexSession
) : AndroidViewModel(application) {

    fun createPaymentMethod(card: PaymentMethod.Card, billing: Billing?): LiveData<PaymentMethodResult> {
        val resultData = MutableLiveData<PaymentMethodResult>()
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
                    resultData.value = PaymentMethodResult.Error(AirwallexCheckoutException(message = errorMessage))
                }
            }
        )
        return resultData
    }

    sealed class PaymentMethodResult {
        data class Success(val paymentMethod: PaymentMethod, val cvc: String) : PaymentMethodResult()
        data class Error(val exception: AirwallexException) : PaymentMethodResult()
    }

    internal class Factory(
        private val application: Application,
        private val airwallex: Airwallex,
        private val session: AirwallexSession
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AddPaymentMethodViewModel(
                application, airwallex, session
            ) as T
        }
    }
}
