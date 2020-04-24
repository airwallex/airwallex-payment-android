package com.airwallex.android

import com.airwallex.android.exception.APIException
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.AirwallexError
import com.airwallex.android.model.PaymentIntent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * The implementation of [PaymentManager] to request the payment.
 */
internal class AirwallexPaymentManager(
    private val repository: ApiRepository,
    private val workScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : PaymentManager {

    /**
     * Confirm the [PaymentIntent] using [ApiRepository.Options]
     */
    override fun confirmPaymentIntent(
        options: ApiRepository.Options,
        listener: Airwallex.PaymentListener<PaymentIntent>
    ) {
        executeApiOperation(ApiOperationType.CONFIRM_PAYMENT_INTENT, options, listener)
    }

    /**
     * Retrieve the [PaymentIntent] using [ApiRepository.Options]
     */
    override fun retrievePaymentIntent(
        options: ApiRepository.Options,
        listener: Airwallex.PaymentListener<PaymentIntent>
    ) {
        executeApiOperation(ApiOperationType.RETRIEVE_PAYMENT_INTENT, options, listener)
    }

    private fun <T> executeApiOperation(
        apiOperationType: ApiOperationType,
        options: ApiRepository.Options,
        listener: Airwallex.PaymentListener<T>
    ) {
        AirwallexApiOperation(
            options,
            repository,
            workScope,
            apiOperationType,
            object : ApiExecutor.ApiResponseListener<AirwallexHttpResponse> {
                override fun onSuccess(response: AirwallexHttpResponse) {
                    if (response.isSuccessful && response.body != null) {
                        val result: T = AirwallexPlugins.gson.fromJson(
                            response.body.string(),
                            classType(apiOperationType)
                        )
                        listener.onSuccess(result)
                    } else {
                        val error = if (response.body != null) AirwallexPlugins.gson.fromJson(
                            response.body.string(),
                            AirwallexError::class.java
                        ) else null
                        listener.onFailed(
                            APIException(
                                message = response.message,
                                traceId = response.allHeaders["x-awx-traceid"],
                                statusCode = response.statusCode,
                                error = error,
                                e = null
                            )
                        )
                    }
                }

                override fun onError(e: AirwallexException) {
                    listener.onFailed(e)
                }
            }
        ).execute()
    }

    private class AirwallexApiOperation(
        private val options: ApiRepository.Options,
        private val repository: ApiRepository,
        workScope: CoroutineScope,
        private val apiOperationType: ApiOperationType,
        listener: ApiResponseListener<AirwallexHttpResponse>
    ) : ApiExecutor<AirwallexHttpResponse>(workScope, listener) {

        override suspend fun getResponse(): AirwallexHttpResponse? {
            return when (apiOperationType) {
                ApiOperationType.CONFIRM_PAYMENT_INTENT -> {
                    repository.confirmPaymentIntent(options)
                }
                ApiOperationType.RETRIEVE_PAYMENT_INTENT -> {
                    repository.retrievePaymentIntent(options)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> classType(type: ApiOperationType): Class<T> {
        when (type) {
            ApiOperationType.CONFIRM_PAYMENT_INTENT,
            ApiOperationType.RETRIEVE_PAYMENT_INTENT -> {
                return PaymentIntent::class.java as Class<T>
            }
        }
    }

    enum class ApiOperationType {
        CONFIRM_PAYMENT_INTENT,
        RETRIEVE_PAYMENT_INTENT
    }
}
