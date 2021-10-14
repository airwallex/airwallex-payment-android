package com.airwallex.android.view

import android.app.Application
import androidx.lifecycle.*
import com.airwallex.android.core.*
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.model.*
import com.airwallex.android.core.util.CurrencyUtils

class AirwallexCheckoutViewModel(
    application: Application,
    private val airwallex: Airwallex,
    private val session: AirwallexSession
) : AndroidViewModel(application) {

    fun checkout(
        paymentMethod: PaymentMethod,
        paymentConsentId: String?,
        cvc: String?,
        additionalInfo: Map<String, String>? = null
    ): LiveData<Result<String>> {
        val resultData = MutableLiveData<Result<String>>()
        airwallex.checkout(
            session,
            paymentMethod,
            paymentConsentId,
            cvc,
            additionalInfo,
            object : Airwallex.PaymentListener<String> {
                override fun onFailed(exception: AirwallexException) {
                    resultData.value = Result.failure(exception)
                }

                override fun onSuccess(response: String) {
                    resultData.value = Result.success(response)
                }
            }
        )
        return resultData
    }

    fun retrieveBanks(paymentMethodTypeName: String): LiveData<Result<BankResponse>> {
        val resultData = MutableLiveData<Result<BankResponse>>()
        when (session) {
            is AirwallexPaymentSession -> {
                val paymentIntent = session.paymentIntent
                airwallex.retrieveBanks(
                    RetrieveBankParams.Builder(
                        clientSecret = requireNotNull(paymentIntent.clientSecret),
                        paymentMethodType = paymentMethodTypeName
                    )
                        .setCountryCode(CurrencyUtils.currencyToCountryMap[session.currency])
                        .build(),
                    object : Airwallex.PaymentListener<BankResponse> {
                        override fun onFailed(exception: AirwallexException) {
                            resultData.value = Result.failure(exception)
                        }

                        override fun onSuccess(response: BankResponse) {
                            resultData.value = Result.success(response)
                        }
                    }
                )
            }
            else -> {
                resultData.value =
                    Result.failure(AirwallexCheckoutException(message = "$paymentMethodTypeName just support one-off payment"))
            }
        }
        return resultData
    }

    fun retrievePaymentMethodTypeInfo(
        paymentMethodTypeName: String
    ): LiveData<Result<PaymentMethodTypeInfo>> {
        val resultData = MutableLiveData<Result<PaymentMethodTypeInfo>>()
        when (session) {
            is AirwallexPaymentSession -> {
                val paymentIntent = session.paymentIntent
                airwallex.retrievePaymentMethodTypeInfo(
                    RetrievePaymentMethodTypeInfoParams.Builder(
                        clientSecret = requireNotNull(paymentIntent.clientSecret),
                        paymentMethodType = paymentMethodTypeName
                    )
                        .setFlow(AirwallexPaymentRequestFlow.IN_APP)
                        .build(),
                    object : Airwallex.PaymentListener<PaymentMethodTypeInfo> {
                        override fun onFailed(exception: AirwallexException) {
                            resultData.value = Result.failure(exception)
                        }

                        override fun onSuccess(response: PaymentMethodTypeInfo) {
                            resultData.value = Result.success(response)
                        }
                    }
                )
            }
            else -> {
                resultData.value =
                    Result.failure(AirwallexCheckoutException(message = "$paymentMethodTypeName just support one-off payment"))
            }
        }

        return resultData
    }

    sealed class BanksResult {
        data class Success(val bankResponse: BankResponse) : BanksResult()

        data class Error(val exception: AirwallexException) : BanksResult()
    }

    sealed class PaymentResult {
        data class Success(val paymentIntentId: String) : PaymentResult()

        data class Error(val exception: AirwallexException) : PaymentResult()
    }

    internal class Factory(
        private val application: Application,
        private val airwallex: Airwallex,
        private val session: AirwallexSession
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AirwallexCheckoutViewModel(
                application, airwallex, session
            ) as T
        }
    }
}
