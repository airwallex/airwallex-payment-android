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
        callback: Airwallex.PaymentCallback<PaymentIntent>
    ) {
        Logger.debug("Start confirm PaymentIntent")
        executeApiOperation(
            ApiOperationType.CONFIRM_PAYMENT_INTENT,
            options,
            null,
            paymentIntentParams,
            callback,
            PaymentIntent::class.java
        )
    }

    /**
     * Retrieve the Airwallex Payment Intent
     */
    override fun retrievePaymentIntent(
        options: AirwallexApiRepository.Options,
        callback: Airwallex.PaymentCallback<PaymentIntent>
    ) {
        Logger.debug("Start retrieve PaymentIntent")
        executeApiOperation(
            ApiOperationType.RETRIEVE_PAYMENT_INTENT,
            options,
            null,
            null,
            callback,
            PaymentIntent::class.java
        )
    }

    /**
     * Create the Airwallex PaymentMethod
     */
    override fun createPaymentMethod(
        options: AirwallexApiRepository.Options,
        paymentMethodParams: PaymentMethodParams,
        callback: Airwallex.PaymentCallback<PaymentMethod>
    ) {
        Logger.debug("Start create PaymentMethod")
        executeApiOperation(
            ApiOperationType.CREATE_PAYMENT_METHOD,
            options,
            paymentMethodParams,
            null,
            callback,
            PaymentMethod::class.java
        )
    }

    /**
     * Get all of customer's PaymentMethods
     */
    override fun getPaymentMethods(
        options: AirwallexApiRepository.Options,
        callback: Airwallex.PaymentCallback<PaymentMethodResponse>
    ) {
        Logger.debug("Get all customer's PaymentMethods")
        executeApiOperation(
            ApiOperationType.GET_PAYMENT_METHODS,
            options,
            null,
            null,
            callback,
            PaymentMethodResponse::class.java
        )
    }

    private fun <T> executeApiOperation(
        apiOperationType: ApiOperationType,
        options: AirwallexApiRepository.Options,
        paymentMethodParams: PaymentMethodParams?,
        paymentIntentParams: PaymentIntentParams?,
        callback: Airwallex.PaymentCallback<T>,
        classOfT: Class<T>
    ) {
        AirwallexApiOperation(
            options,
            paymentMethodParams,
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
        private val paymentMethodParams: PaymentMethodParams?,
        private val paymentIntentParams: PaymentIntentParams?,
        private val repository: ApiRepository,
        workScope: CoroutineScope,
        callback: ApiResultCallback<AirwallexHttpResponse>,
        private val apiOperationType: ApiOperationType
    ) : ApiOperation<AirwallexHttpResponse>(workScope, callback) {

        override suspend fun getResult(): AirwallexHttpResponse? {
            return when (apiOperationType) {
                ApiOperationType.CREATE_PAYMENT_METHOD -> {
                    repository.createPaymentMethod(
                        options,
                        requireNotNull(paymentMethodParams)
                    )
                }
                ApiOperationType.GET_PAYMENT_METHODS -> {
                    repository.getPaymentMethods(options)
                }
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
        CREATE_PAYMENT_METHOD,
        GET_PAYMENT_METHODS,
        CONFIRM_PAYMENT_INTENT,
        RETRIEVE_PAYMENT_INTENT
    }
}
