package com.airwallex.android.view

import android.app.Application
import androidx.lifecycle.*
import com.airwallex.android.Airwallex
import com.airwallex.android.AirwallexSession
import com.airwallex.android.ClientSecretRepository
import com.airwallex.android.model.Billing
import com.airwallex.android.model.ClientSecret
import com.airwallex.android.model.CreatePaymentMethodParams
import com.airwallex.android.model.PaymentMethod

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

                            override fun onFailed(exception: Exception) {
                                resultData.value = PaymentMethodResult.Error(exception)
                            }
                        }
                    )
                }

                override fun onClientSecretError(errorMessage: String) {
                    resultData.value = PaymentMethodResult.Error(Exception(errorMessage))
                }
            }
        )
        return resultData
    }

    sealed class PaymentMethodResult {
        data class Success(val paymentMethod: PaymentMethod, val cvc: String) : PaymentMethodResult()
        data class Error(val exception: Exception) : PaymentMethodResult()
    }

    internal class Factory(
        private val application: Application,
        private val airwallex: Airwallex,
        private val session: AirwallexSession
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AddPaymentMethodViewModel(
                application, airwallex, session
            ) as T
        }
    }
}
