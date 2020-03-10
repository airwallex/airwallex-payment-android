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

    override fun confirmPaymentIntent(
        options: AirwallexApiRepository.Options,
        paymentIntentParams: PaymentIntentParams,
        callback: Airwallex.PaymentCallback<PaymentIntent>
    ) {
        ConfirmIntentTask(
            options,
            paymentIntentParams,
            repository,
            workScope,
            object : ApiResultCallback<AirwallexHttpResponse> {
                override fun onSuccess(result: AirwallexHttpResponse) {
                    handleResponse(result, callback, PaymentIntent::class.java)
                }

                override fun onError(e: AirwallexException) {
                    callback.onFailed(e)
                }
            }
        ).execute()
    }

    override fun retrievePaymentIntent(
        options: AirwallexApiRepository.Options,
        callback: Airwallex.PaymentCallback<PaymentIntent>
    ) {
        RetrieveIntentTask(
            options,
            repository,
            workScope,
            object : ApiResultCallback<AirwallexHttpResponse> {
                override fun onSuccess(result: AirwallexHttpResponse) {
                    handleResponse(result, callback, PaymentIntent::class.java)
                }

                override fun onError(e: AirwallexException) {
                    callback.onFailed(e)
                }
            }
        ).execute()
    }

    override fun createPaymentMethod(
        options: AirwallexApiRepository.Options,
        paymentMethodParams: PaymentMethodParams,
        callback: Airwallex.PaymentCallback<PaymentMethod>
    ) {
        CreatePaymentMethodTask(
            options,
            paymentMethodParams,
            repository,
            workScope,
            object : ApiResultCallback<AirwallexHttpResponse> {
                override fun onSuccess(result: AirwallexHttpResponse) {
                    handleResponse(result, callback, PaymentMethod::class.java)
                }

                override fun onError(e: AirwallexException) {
                    callback.onFailed(e)
                }
            }
        ).execute()
    }

    override fun getPaymentMethods(
        options: AirwallexApiRepository.Options,
        callback: Airwallex.PaymentCallback<PaymentMethodResponse>
    ) {
        GetPaymentMethodsTask(
            options,
            repository,
            workScope,
            object : ApiResultCallback<AirwallexHttpResponse> {
                override fun onSuccess(result: AirwallexHttpResponse) {
                    handleResponse(result, callback, PaymentMethodResponse::class.java)
                }

                override fun onError(e: AirwallexException) {
                    callback.onFailed(e)
                }
            }
        ).execute()
    }

    private fun <T> handleResponse(
        result: AirwallexHttpResponse,
        callback: Airwallex.PaymentCallback<T>,
        classOfT: Class<T>
    ) {
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

    private class ConfirmIntentTask(
        private val options: AirwallexApiRepository.Options,
        private val params: PaymentIntentParams,
        private val repository: ApiRepository,
        workScope: CoroutineScope,
        callback: ApiResultCallback<AirwallexHttpResponse>
    ) : ApiOperation<AirwallexHttpResponse>(workScope, callback) {

        override suspend fun getResult(): AirwallexHttpResponse? {
            return repository.confirmPaymentIntent(options, params)
        }
    }

    private class RetrieveIntentTask(
        private val options: AirwallexApiRepository.Options,
        private val repository: ApiRepository,
        workScope: CoroutineScope,
        callback: ApiResultCallback<AirwallexHttpResponse>
    ) : ApiOperation<AirwallexHttpResponse>(workScope, callback) {

        override suspend fun getResult(): AirwallexHttpResponse? {
            return repository.retrievePaymentIntent(options)
        }
    }

    private class CreatePaymentMethodTask(
        private val options: AirwallexApiRepository.Options,
        private val params: PaymentMethodParams,
        private val repository: ApiRepository,
        workScope: CoroutineScope,
        callback: ApiResultCallback<AirwallexHttpResponse>
    ) : ApiOperation<AirwallexHttpResponse>(workScope, callback) {

        override suspend fun getResult(): AirwallexHttpResponse? {
            return repository.createPaymentMethod(options, params)
        }
    }

    private class GetPaymentMethodsTask(
        private val options: AirwallexApiRepository.Options,
        private val repository: ApiRepository,
        workScope: CoroutineScope,
        callback: ApiResultCallback<AirwallexHttpResponse>
    ) : ApiOperation<AirwallexHttpResponse>(workScope, callback) {

        override suspend fun getResult(): AirwallexHttpResponse? {
            return repository.getPaymentMethods(options)
        }
    }
}
