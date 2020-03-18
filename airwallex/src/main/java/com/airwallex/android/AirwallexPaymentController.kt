package com.airwallex.android

import com.airwallex.android.exception.APIException
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.AirwallexError
import com.airwallex.android.model.PaymentIntent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Suppress("UNCHECKED_CAST")
internal class AirwallexPaymentController(
    private val repository: ApiRepository,
    private val workScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : PaymentController {

    /**
     * Confirm the Airwallex PaymentIntent
     */
    override fun confirmPaymentIntent(
        options: AirwallexApiRepository.Options,
        listener: Airwallex.PaymentListener<PaymentIntent>
    ) {
        Logger.debug("Start confirm PaymentIntent")
        executeApiOperation(ApiOperationType.CONFIRM_PAYMENT_INTENT, options, listener)
    }

    /**
     * Retrieve the Airwallex Payment Intent
     */
    override fun retrievePaymentIntent(
        options: AirwallexApiRepository.Options,
        listener: Airwallex.PaymentListener<PaymentIntent>
    ) {
        Logger.debug("Start retrieve PaymentIntent")
        executeApiOperation(ApiOperationType.RETRIEVE_PAYMENT_INTENT, options, listener)
    }

    private fun <T> executeApiOperation(
        apiOperationType: ApiOperationType,
        options: AirwallexApiRepository.Options,
        listener: Airwallex.PaymentListener<T>
    ) {
        AirwallexApiOperation(
            options,
            repository,
            workScope,
            object : ApiResultCallback<AirwallexHttpResponse> {
                override fun onSuccess(result: AirwallexHttpResponse) {
                    if (result.isSuccessful && result.body != null) {
                        val response: T = AirwallexPlugins.gson.fromJson(
                            result.body.string(),
                            classType(apiOperationType)
                        )
                        listener.onSuccess(response)
                    } else {
                        val error = if (result.body != null) AirwallexPlugins.gson.fromJson(
                            result.body.string(),
                            AirwallexError::class.java
                        ) else null
                        listener.onFailed(
                            APIException(
                                message = result.message,
                                traceId = result.allHeaders["x-awx-traceid"],
                                statusCode = result.statusCode,
                                error = error,
                                e = null
                            )
                        )
                    }
                }

                override fun onError(e: AirwallexException) {
                    listener.onFailed(e)
                }
            },
            apiOperationType
        ).execute()
    }

    private class AirwallexApiOperation(
        private val options: AirwallexApiRepository.Options,
        private val repository: ApiRepository,
        workScope: CoroutineScope,
        callback: ApiResultCallback<AirwallexHttpResponse>,
        private val apiOperationType: ApiOperationType
    ) : ApiOperation<AirwallexHttpResponse>(workScope, callback) {

        override suspend fun getResult(): AirwallexHttpResponse? {
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
