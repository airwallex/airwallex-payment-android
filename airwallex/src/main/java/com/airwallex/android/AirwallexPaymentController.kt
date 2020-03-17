package com.airwallex.android

import com.airwallex.android.exception.APIException
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

internal class AirwallexPaymentController(
    private val repository: ApiRepository,
    private val workScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : PaymentController {

    /**
     * Confirm the Airwallex PaymentIntent
     */
    override fun confirmPaymentIntent(
        options: AirwallexApiRepository.Options,
        paymentIntentParams: PaymentIntentParams,
        listener: Airwallex.PaymentListener<PaymentIntent>
    ) {
        Logger.debug("Start confirm PaymentIntent")
        executeApiOperation(
            ApiOperationType.CONFIRM_PAYMENT_INTENT,
            options,
            paymentIntentParams,
            listener,
            PaymentIntent::class.java
        )
    }

    /**
     * Retrieve the Airwallex Payment Intent
     */
    override fun retrievePaymentIntent(
        options: AirwallexApiRepository.Options,
        listener: Airwallex.PaymentListener<PaymentIntent>
    ) {
        Logger.debug("Start retrieve PaymentIntent")
        executeApiOperation(
            ApiOperationType.RETRIEVE_PAYMENT_INTENT,
            options,
            null,
            listener,
            PaymentIntent::class.java
        )
    }

    private fun <T> executeApiOperation(
        apiOperationType: ApiOperationType,
        options: AirwallexApiRepository.Options,
        paymentIntentParams: PaymentIntentParams?,
        callback: Airwallex.PaymentListener<T>,
        classOfT: Class<T>
    ) {
        AirwallexApiOperation(
            options,
            paymentIntentParams,
            repository,
            workScope,
            object : ApiResultCallback<AirwallexHttpResponse> {
                override fun onSuccess(result: AirwallexHttpResponse) {
                    if (result.isSuccessful && result.body != null) {
                        val response: T = AirwallexPlugins.gson.fromJson(
                            result.body.string(),
                            classOfT
                        )
                        callback.onSuccess(response)
                    } else {
                        val error = if (result.body != null) AirwallexPlugins.gson.fromJson(
                            result.body.string(),
                            AirwallexError::class.java
                        ) else null
                        callback.onFailed(
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
                    callback.onFailed(e)
                }
            },
            apiOperationType
        ).execute()
    }

    private class AirwallexApiOperation(
        private val options: AirwallexApiRepository.Options,
        private val paymentIntentParams: PaymentIntentParams?,
        private val repository: ApiRepository,
        workScope: CoroutineScope,
        callback: ApiResultCallback<AirwallexHttpResponse>,
        private val apiOperationType: ApiOperationType
    ) : ApiOperation<AirwallexHttpResponse>(workScope, callback) {

        override suspend fun getResult(): AirwallexHttpResponse? {
            return when (apiOperationType) {
                ApiOperationType.CONFIRM_PAYMENT_INTENT -> {
                    repository.confirmPaymentIntent(
                        options,
                        requireNotNull(paymentIntentParams)
                    )
                }
                ApiOperationType.RETRIEVE_PAYMENT_INTENT -> {
                    repository.retrievePaymentIntent(options)
                }
            }
        }
    }

    enum class ApiOperationType {
        CONFIRM_PAYMENT_INTENT,
        RETRIEVE_PAYMENT_INTENT
    }
}
