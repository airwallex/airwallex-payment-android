package com.airwallex.android.view

import android.app.Application
import androidx.lifecycle.*
import com.airwallex.android.*
import com.airwallex.android.model.*
import com.airwallex.android.model.RetrieveAvailablePaymentMethodParams
import java.util.concurrent.atomic.AtomicInteger

internal class PaymentMethodsViewModel(
    application: Application,
    private val airwallex: Airwallex,
    private val session: AirwallexSession
) : AndroidViewModel(application) {

    val paymentIntent: PaymentIntent? by lazy {
        when (session) {
            is AirwallexPaymentSession -> {
                session.paymentIntent
            }
            is AirwallexRecurringWithIntentSession -> {
                session.paymentIntent
            }
            is AirwallexRecurringSession -> {
                null
            }
            else -> {
                throw Exception("Not supported session $session")
            }
        }
    }

    fun fetchPaymentMethodTypes(): LiveData<PaymentMethodTypeResult> {
        val resultData = MutableLiveData<PaymentMethodTypeResult>()
        when (session) {
            is AirwallexPaymentSession, is AirwallexRecurringWithIntentSession -> {
                retrieveAvailablePaymentMethods(
                    mutableListOf(),
                    AtomicInteger(0),
                    resultData,
                    requireNotNull(paymentIntent?.clientSecret)
                )
            }
            is AirwallexRecurringSession -> {
                ClientSecretRepository.getInstance().retrieveClientSecret(
                    requireNotNull(session.customerId),
                    object : ClientSecretRepository.ClientSecretRetrieveListener {
                        override fun onClientSecretRetrieve(clientSecret: ClientSecret) {
                            retrieveAvailablePaymentMethods(
                                mutableListOf(),
                                AtomicInteger(0),
                                resultData,
                                clientSecret.value
                            )
                        }

                        override fun onClientSecretError(errorMessage: String) {
                            resultData.value =
                                PaymentMethodTypeResult.Error(Exception(errorMessage))
                        }
                    }
                )
            }
        }
        return resultData
    }

    private fun retrieveAvailablePaymentMethods(
        availablePaymentMethodList: MutableList<AvailablePaymentMethod>,
        availablePaymentMethodPageNum: AtomicInteger,
        resultData: MutableLiveData<PaymentMethodTypeResult>,
        clientSecret: String
    ) {
        airwallex.retrieveAvailablePaymentMethods(
            params = RetrieveAvailablePaymentMethodParams.Builder(
                clientSecret = clientSecret,
                pageNum = availablePaymentMethodPageNum.get()
            )
                .setActive(true)
                .setTransactionCurrency(session.currency)
                .build(),
            listener = object : Airwallex.PaymentListener<AvailablePaymentMethodResponse> {
                override fun onFailed(exception: Exception) {
                    resultData.value = PaymentMethodTypeResult.Error(exception)
                }

                override fun onSuccess(response: AvailablePaymentMethodResponse) {
                    availablePaymentMethodPageNum.incrementAndGet()
                    availablePaymentMethodList.addAll(response.items ?: emptyList())
                    if (response.hasMore) {
                        retrieveAvailablePaymentMethods(
                            availablePaymentMethodList,
                            availablePaymentMethodPageNum,
                            resultData,
                            clientSecret
                        )
                    } else {
                        when (session) {
                            is AirwallexRecurringSession, is AirwallexRecurringWithIntentSession -> {
                                resultData.value =
                                    PaymentMethodTypeResult.Success(
                                        availablePaymentMethodList.filter { it.transactionMode == AvailablePaymentMethod.TransactionMode.RECURRING }
                                            .mapNotNull { it.name }.distinct()
                                    )
                            }
                            is AirwallexPaymentSession -> {
                                resultData.value =
                                    PaymentMethodTypeResult.Success(
                                        availablePaymentMethodList.filter { it.transactionMode == AvailablePaymentMethod.TransactionMode.ONE_OFF }
                                            .mapNotNull { it.name }.distinct()
                                    )
                            }
                            else ->
                                resultData.value =
                                    PaymentMethodTypeResult.Error(Exception("Not support session $session"))
                        }
                    }
                }
            }
        )
    }

    fun deletePaymentConsent(paymentConsent: PaymentConsent): LiveData<PaymentConsentResult> {
        val resultData = MutableLiveData<PaymentConsentResult>()
        ClientSecretRepository.getInstance().retrieveClientSecret(
            requireNotNull(session.customerId),
            object : ClientSecretRepository.ClientSecretRetrieveListener {
                override fun onClientSecretRetrieve(clientSecret: ClientSecret) {
                    airwallex.disablePaymentConsent(
                        DisablePaymentConsentParams(
                            clientSecret = clientSecret.value,
                            paymentConsentId = requireNotNull(paymentConsent.id),
                        ),
                        object : Airwallex.PaymentListener<PaymentConsent> {

                            override fun onFailed(exception: Exception) {
                                resultData.value = PaymentConsentResult.Error(exception)
                            }

                            override fun onSuccess(response: PaymentConsent) {
                                resultData.value = PaymentConsentResult.Success(response)
                            }
                        }
                    )
                }

                override fun onClientSecretError(errorMessage: String) {
                    resultData.value = PaymentConsentResult.Error(Exception(errorMessage))
                }
            }
        )
        return resultData
    }

    internal sealed class PaymentConsentResult {
        data class Success(val paymentConsent: PaymentConsent) : PaymentConsentResult()
        data class Error(val exception: Exception) : PaymentConsentResult()
    }

    internal sealed class PaymentMethodTypeResult {
        data class Success(val availableThirdPaymentTypes: List<PaymentMethodType>) :
            PaymentMethodTypeResult()

        data class Error(val exception: Exception) : PaymentMethodTypeResult()
    }

    internal class Factory(
        private val application: Application,
        private val airwallex: Airwallex,
        private val session: AirwallexSession
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PaymentMethodsViewModel(
                application,
                airwallex,
                session
            ) as T
        }
    }
}
