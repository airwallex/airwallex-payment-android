package com.airwallex.android.view

import android.app.Application
import androidx.lifecycle.*
import com.airwallex.android.core.*
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.model.*
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

    fun fetchAvailablePaymentMethodTypes(): LiveData<Result<List<AvailablePaymentMethodType>>> {
        val resultData = MutableLiveData<Result<List<AvailablePaymentMethodType>>>()
        when (session) {
            is AirwallexPaymentSession, is AirwallexRecurringWithIntentSession -> {
                retrieveAvailablePaymentMethods(
                    resultData = resultData,
                    clientSecret = requireNotNull(paymentIntent?.clientSecret)
                )
            }
            is AirwallexRecurringSession -> {
                try {
                    ClientSecretRepository.getInstance().retrieveClientSecret(
                        requireNotNull(session.customerId),
                        object : ClientSecretRepository.ClientSecretRetrieveListener {
                            override fun onClientSecretRetrieve(clientSecret: ClientSecret) {
                                retrieveAvailablePaymentMethods(
                                    resultData = resultData,
                                    clientSecret = clientSecret.value
                                )
                            }

                            override fun onClientSecretError(errorMessage: String) {
                                resultData.value =
                                    Result.failure(AirwallexCheckoutException(message = errorMessage))
                            }
                        }
                    )
                } catch (e: AirwallexCheckoutException) {
                    resultData.value = Result.failure(e)
                }
            }
        }
        return resultData
    }

    private fun retrieveAvailablePaymentMethods(
        availablePaymentMethodList: MutableList<AvailablePaymentMethodType> = mutableListOf(),
        availablePaymentMethodPageNum: AtomicInteger = AtomicInteger(0),
        resultData: MutableLiveData<Result<List<AvailablePaymentMethodType>>>,
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
            listener = object : Airwallex.PaymentListener<AvailablePaymentMethodTypeResponse> {
                override fun onFailed(exception: AirwallexException) {
                    resultData.value = Result.failure(exception)
                }

                override fun onSuccess(response: AvailablePaymentMethodTypeResponse) {
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
                                    Result.success(
                                        availablePaymentMethodList.filter {
                                            it.transactionMode == TransactionMode.RECURRING
                                        }
                                    )
                            }
                            is AirwallexPaymentSession -> {
                                resultData.value =
                                    Result.success(
                                        availablePaymentMethodList.filter {
                                            it.transactionMode == TransactionMode.ONE_OFF
                                        }
                                    )
                            }
                            else -> {
                                resultData.value =
                                    Result.failure(AirwallexCheckoutException(message = "Not support session $session"))
                            }
                        }
                    }
                }
            }
        )
    }

    fun deletePaymentConsent(paymentConsent: PaymentConsent): LiveData<Result<PaymentConsent>> {
        val resultData = MutableLiveData<Result<PaymentConsent>>()
        try {
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

                                override fun onFailed(exception: AirwallexException) {
                                    resultData.value = Result.failure(exception)
                                }

                                override fun onSuccess(response: PaymentConsent) {
                                    resultData.value = Result.success(response)
                                }
                            }
                        )
                    }

                    override fun onClientSecretError(errorMessage: String) {
                        resultData.value =
                            Result.failure(AirwallexCheckoutException(message = errorMessage))
                    }
                }
            )
        } catch (e: AirwallexCheckoutException) {
            resultData.value = Result.failure(e)
        }
        return resultData
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
