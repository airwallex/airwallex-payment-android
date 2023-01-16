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

    fun filterRequiredFields(info: PaymentMethodTypeInfo): List<DynamicSchemaField>? {
        return info
            .fieldSchemas
            ?.firstOrNull { schema -> schema.transactionMode == TransactionMode.ONE_OFF }
            ?.fields
            ?.filter { !it.hidden }
    }

    fun fetchPaymentFlow(info: PaymentMethodTypeInfo): AirwallexPaymentRequestFlow {
        val flowField = info
            .fieldSchemas
            ?.firstOrNull { schema -> schema.transactionMode == TransactionMode.ONE_OFF }
            ?.fields
            ?.firstOrNull { it.name == FLOW }

        val candidates = flowField?.candidates
        return when {
            candidates?.find { it.value == AirwallexPaymentRequestFlow.IN_APP.value } != null -> {
                AirwallexPaymentRequestFlow.IN_APP
            }
            candidates != null && candidates.isNotEmpty() -> {
                AirwallexPaymentRequestFlow.fromValue(candidates[0].value)
                    ?: AirwallexPaymentRequestFlow.IN_APP
            }
            else -> {
                AirwallexPaymentRequestFlow.IN_APP
            }
        }
    }

    suspend fun fetchAvailablePaymentMethodTypes(): LiveData<Result<List<AvailablePaymentMethodType>>> {
        val resultData = MutableLiveData<Result<List<AvailablePaymentMethodType>>>()
        when (session) {
            is AirwallexPaymentSession, is AirwallexRecurringWithIntentSession -> {
                paymentIntent?.clientSecret
            }
            is AirwallexRecurringSession -> {
                try {
                    ClientSecretRepository.getInstance()
                        .retrieveClientSecret(requireNotNull(session.customerId))
                        .value
                } catch (e: AirwallexCheckoutException) {
                    resultData.value = Result.failure(e)
                    null
                }
            }
            else -> null
        }?.let { clientSecret ->
            TokenManager.updateClientSecret(clientSecret)
            retrieveAvailablePaymentMethods(
                resultData = resultData,
                clientSecret = clientSecret
            )
        }
        return resultData
    }

    private suspend fun retrieveAvailablePaymentMethods(
        availablePaymentMethodList: MutableList<AvailablePaymentMethodType> = mutableListOf(),
        availablePaymentMethodPageNum: AtomicInteger = AtomicInteger(0),
        resultData: MutableLiveData<Result<List<AvailablePaymentMethodType>>>,
        clientSecret: String
    ) {
        val response = try {
            airwallex.retrieveAvailablePaymentMethods(
                session = session,
                params = RetrieveAvailablePaymentMethodParams.Builder(
                    clientSecret = clientSecret,
                    pageNum = availablePaymentMethodPageNum.get()
                )
                    .setActive(true)
                    .setTransactionCurrency(session.currency)
                    .setCountryCode(session.countryCode)
                    .build()
            )
        } catch (exception: AirwallexException) {
            resultData.value = Result.failure(exception)
            return
        }
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
            resultData.value = Result.success(availablePaymentMethodList)
        }
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
                                    resultData.value = Result.success(paymentConsent)
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

    fun hasSinglePaymentMethod(
        desiredPaymentMethodType: AvailablePaymentMethodType?,
        paymentMethods: List<AvailablePaymentMethodType>,
        consents: List<PaymentConsent>
    ): Boolean {
        if (desiredPaymentMethodType == null) return false

        val hasPaymentConsents = consents.isNotEmpty()
        val availablePaymentMethodsSize = paymentMethods.size

        return !hasPaymentConsents && availablePaymentMethodsSize == 1
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

    companion object {
        const val COUNTRY_CODE = "country_code"
        const val FLOW = "flow"
    }
}
