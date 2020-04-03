package com.airwallex.android

import com.airwallex.android.exception.APIException
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.AirwallexError
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.PaymentMethodResponse
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

    override fun createPaymentMethod(
        options: ApiRepository.Options,
        listener: Airwallex.PaymentListener<PaymentMethod>
    ) {
        executeApiOperation(ApiOperationType.CREATE_PAYMENT_METHOD, options, listener)
    }

    override fun retrievePaymentMethods(
        options: ApiRepository.Options,
        listener: Airwallex.PaymentListener<PaymentMethodResponse>
    ) {
        executeApiOperation(ApiOperationType.RETRIEVE_PAYMENT_METHOD, options, listener)
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
                ApiOperationType.CREATE_PAYMENT_METHOD -> {
                    repository.createPaymentMethod(options)
                }
                ApiOperationType.RETRIEVE_PAYMENT_METHOD -> {
                    repository.retrievePaymentMethods(options)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> classType(type: ApiOperationType): Class<T> {
        return when (type) {
            ApiOperationType.CONFIRM_PAYMENT_INTENT,
            ApiOperationType.RETRIEVE_PAYMENT_INTENT -> {
                PaymentIntent::class.java as Class<T>
            }
            ApiOperationType.CREATE_PAYMENT_METHOD -> {
                PaymentMethod::class.java as Class<T>
            }
            ApiOperationType.RETRIEVE_PAYMENT_METHOD -> {
                PaymentMethodResponse::class.java as Class<T>
            }
        }
    }

    enum class ApiOperationType {
        CONFIRM_PAYMENT_INTENT,
        RETRIEVE_PAYMENT_INTENT,
        CREATE_PAYMENT_METHOD,
        RETRIEVE_PAYMENT_METHOD
    }
}
